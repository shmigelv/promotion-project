pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 1, unit: 'HOURS')
        timestamps()
    }

    tools {
        git 'Default'
    }

    environment {
        AWS_ECR_REGION = 'eu-west-1'
    }

    stages {
        stage('Clone git') {
            steps {
                git branch: 'develop_v1', url: 'https://github.com/shmigelv/promotion-project'
            }
        }
        stage('Build & Test') {
            steps {
                sh "mvn clean package -Dmaven.test.skip=true"
            }
        }

        stage('Push Image to ECR and redeploy on ECS') {
            steps {
                sh(label: 'ECR login and docker push', script:
                     '''
                     #!/bin/bash
                        set +x

                        # Authenticate with ECR
                        eval $(aws ecr get-login --region "us-east-2" --no-include-email)

                        # Delete old ECR image
                        aws ecr batch-delete-image --repository-name promotion-project --image-ids imageTag=latest

                        # Build new docker image and push it to ECR
                        docker build  -t promotion-project .
                        docker tag promotion-project:latest 352245596834.dkr.ecr.us-east-2.amazonaws.com/promotion-project:latest
                        docker push 352245596834.dkr.ecr.us-east-2.amazonaws.com/promotion-project:latest
                     '''.stripIndent())
            }
        }
    }
}