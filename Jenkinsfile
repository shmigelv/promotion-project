pipeline {
    agent any

    triggers {
        pollSCM '* * * * *'
    }
    stages {
        stage('Build') {
            steps {
                sh './mvn compile'
            }
        }
        stage('Test') {
            steps {
                sh './test test'
            }
        }
    }
}