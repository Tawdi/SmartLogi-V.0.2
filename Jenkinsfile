pipeline {
    agent any

    // Définir les outils globaux configurés dans Jenkins (Manage Jenkins > Tools)
    tools {
        maven 'Maven3'    // Nom exact que tu as donné dans les Tools
        jdk   'JDK-JNK'     // Nom exact de ton JDK (Java 21 recommandé pour Spring Boot 3+)
    }

    environment {
        // À adapter avec ton nom Docker Hub et ton repo
        IMAGE_NAME = 'tawdi/smartlogi'   // change par ton username Docker Hub
        IMAGE_TAG  = "0.1"
        DOCKER_CREDS = 'docker-hub-credentials'  // ID des credentials dans Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Récupération du code depuis GitHub...'
                git branch: 'main',
                    url: 'https://github.com/Tawdi/SmartLogi-V.0.2.git',
//                     credentialsId: 'github-credentials'  // optionnel si repo public
            }
        }

        stage('Build & Test') {
            steps {
                echo 'Compilation et exécution des tests...'
                sh 'mvn clean verify'
            }
            post {
                always {
                    // Publication des résultats de tests dans Jenkins
                    junit 'target/surefire-reports/**/*.xml'
                    // Archive le JAR pour traçabilité
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Construction de l’image Docker...'
                // Spring Boot 3+ avec Buildpacks (recommandé, pas besoin de Dockerfile)
                sh 'mvn spring-boot:build-image -Dspring-boot.build-image.imageName=${IMAGE_NAME}:${IMAGE_TAG}'

                // Alternative si tu as un Dockerfile classique :
                // sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Push Docker Image') {
            when {
                branch 'main'  // uniquement sur la branche principale
            }
            steps {
                echo 'Push de l’image sur Docker Hub...'
                withDockerRegistry([credentialsId: DOCKER_CREDS, url: '']) {
                    sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
                    sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker push ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy for Test') {
            when {
                branch 'main'
            }
            steps {
                echo 'Déploiement simple pour vérification...'
                // Arrête et supprime l’ancien container s’il existe
                sh '''
                    docker stop smartlogi-test || true
                    docker rm smartlogi-test || true
                '''
                // Lance le nouveau
                sh '''
                    docker run -d --name smartlogi-test -p 8081:8080 ${IMAGE_NAME}:${IMAGE_TAG}
                '''
                echo 'Application déployée sur http://localhost:8081'
            }
        }
    }

    post {
        success {
            echo 'Pipeline réussie avec succès ! 🚀 Ton application est prête.'
        }
        failure {
            echo 'Échec de la pipeline. Vérifie les logs ci-dessus.'
        }
        always {
            // Nettoyage optionnel des images locales pour éviter l’encombrement
            sh 'docker system prune -f || true'
        }
    }
}