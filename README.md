# TODO Project 

<!-- TOC -->
* [Description](#description)
* [Required tools](#required-tools)
* [Preconfiguring Google Cloud](#preconfiguring-google-cloud)
* [Dealing with secrets](#dealing-with-secrets)
* [How to deploy manually](#how-to-deploy-manually)
<!-- TOC -->

## Description

This is a project for https://devopswithkubernetes.com/ course.

It was separated from the [base repo](https://github.com/kat-ushka/devops-with-kubernetes-course) to support GKE deployment pipeline.

## Required tools

To successfully deploy this project one will need to install:
* [kubectl](https://kubernetes.io/docs/tasks/tools/)
* [age](https://github.com/FiloSottile/age)
* [sops](https://github.com/mozilla/sops)
* [Google Cloud SDK](https://cloud.google.com/sdk/docs/install)

## Preconfiguring Google Cloud

1. Create a new project named `dwk-gke` on [resources page](https://console.cloud.google.com/cloud-resource-manager).
2. Install Google CLoud SDK (instruction is [here](https://cloud.google.com/sdk/docs/install))
3. After completing the initialization set the previously created project to be used:
    ```shell
    gcloud config set project dwk-gke
    ```
4. Enable the container.googleapis.com service:
    ```shell
    gcloud services enable container.googleapis.com
    ```
5. Create a new  kubernetes cluster:
    ```shell
    gcloud container clusters create dwk-cluster --zone=europe-north1-b --cluster-version=1.22
    ```

## Dealing with secrets

### Kubernetes

Postgres' environment variables are stored sops-encrypted in the file [secret.enc.yaml](./manifests/secret.enc.yaml).

The file is decrypted automatically in GitHub Pipeline with the help of GKE_DWK_SOPS_AGE_KEY repo's action secret where the age key.txt is stored.

It can be done manually using following steps:

1. Create a secret.yaml file like this:
    ```yaml
    apiVersion: v1
    kind: Secret
    metadata:
      namespace: log-output
      name: postgres-secret-config
    type: Opaque
    data:
      POSTGRES_PASSWORD: <base64 password>
      POSTGRES_USER: <base64 username>
      POSTGRES_DB: <base64 db name>
    ```
   
   > TIP  
   > You can use following shell command to get base64-encoded text:
   > ```shell
   > echo <text_to_encode> | base64
   > ```
2. Create an age key:
   ```shell
   age-keygen -o key.txt
   export SOPS_AGE_KEY_FILE=$(pwd)/key.txt
   ```
3. Encrypt secret.yaml with sops using the new key:
   ```shell
   sops --encrypt --age <public key from the previous step> --encrypted-regex '^(data)$' secret.yaml > secret.enc.yaml
   ```
4. Store this file in a git repo. Do not lose key.txt!
5. Use following script to decrypt and create kubernetes secret (define SOPS_AGE_KEY_FILE again if needed):
   ```shell
   sops --decrypt secret.enc.yaml | kubectl apply -f -
   ```

### GitHub

The following GitHub actions secrets should exist in repository to support pipeline:

- GKE_DWK_PROJECT - the ID of the Google Cloud project  
- GKE_SA_KEY - is a service account key that is required to access the Google Cloud services - read their guide for it [here](https://cloud.google.com/iam/docs/creating-managing-service-account-keys)
   > These roles are more than enough to do the deployment. Give them to the service account:
   > - Kubernetes Engine Service Agent
   > - Kubernetes Engine Cluster Viewer
   > - Storage Admin
- GKE_DWK_SOPS_AGE_KEY - an age key. For more information check [this section](#Kubernetes).

## How to deploy manually

Assuming you have kubectl, age, sops, and Google Cloud SDK already installed and configured.

If not check [this section](#Required tools) for the installation links and [this one](#Preconfiguring Google Cloud) for Google Cloud prerequisites.

1. Create a new kubernetes cluster:
    ```shell
    gcloud container clusters create dwk-cluster --zone=europe-north1-b --cluster-version=1.22
    ```
   or switch to an existing one:
   ```shell
   gcloud container clusters get-credentials dwk-cluster --zone=europe-north1-b
   ```
2. Create a namespace:
    ```shell
    kubectl apply -f manifests/0.namespace.yaml
    ```
3. Create a postgres secret config (check this [section](#Dealing with secrets)):
    ```shell
    sops --decrypt manifests/2.secret.enc.yaml | kubectl apply -f -
    ``` 
4. Apply other manifests (don't mind the error with secret.enc.yaml):
    ```shell
    kubectl apply -f manifests
    ```
5. Check created ingress object to know the ip:
    ```shell
    kubectl get ing -n log-output
    ```
6. Visit http://<to-do-web-ingress ip>:<to-do-web-ingress port>/todo to use service.
7. Delete the cluster after using to avoid using up the credits:
     ```shell
     gcloud container clusters delete dwk-cluster --zone=europe-north1-b
     ```