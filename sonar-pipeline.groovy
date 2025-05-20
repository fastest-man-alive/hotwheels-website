pipeline {
    agent any
    environment {
        SONARQUBE_SERVER='sonarqube'
    }
    stages {
        stage('Git') {
            steps {
                git branch: 'main', changelog: false, credentialsId: 'github-sa', poll: false, url: 'https://github.com/fastest-man-alive/hotwheels-website.git'
            }
        }
        stage('SonarQube Analysis') {
            tools {
                jdk "jdk11" // the name you have given the JDK installation in Global Tool Configuration
            }
            environment {
                scannerHome = tool 'sonar-scanner' // the name you have given the Sonar Scanner (in Global Tool Configuration)
            }
            steps {
            withSonarQubeEnv(SONARQUBE_SERVER) {
                sh "${scannerHome}/bin/sonar-scanner  -Dsonar.projectKey=hotwheels-website -Dsonar.sources=."
            }
            }
        }
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                    }
                }
            }
        
    }
}