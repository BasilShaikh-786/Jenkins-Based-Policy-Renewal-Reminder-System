pipeline {
    agent any

    tools {
        // Maven tool configured in Jenkins Global Tool Configuration
        maven 'Maven-3.9.x'
        // JDK tool configured in Jenkins Global Tool Configuration
        jdk 'JDK-21'
    }

    options {
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '15'))
    }

    environment {
        PROJECT_NAME = 'Jenkins-Based Policy Renewal Reminder System'
        STUDENT_NAME = 'Basil Shaikh'
        ROLL_NUMBER  = '23102C0026'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Fetching latest revision from Git repository..."
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                echo "Compiling Spring Boot source code..."
                sh 'mvn clean compile'
            }
        }

        stage('Unit & Integration Tests') {
            steps {
                echo "Executing JUnit 5 test suite and verifying business rules (BR-01 through BR-07)..."
                sh 'mvn test'
            }
            post {
                always {
                    // Record and publish JUnit test result XMLs
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Package') {
            steps {
                echo "Packaging runnable JAR artifact..."
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    // Archive generated JAR file
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('DevOps Notification / Reminder Check') {
            steps {
                echo "Simulating automated policy expiry check and reminder notification trigger..."
                echo "Pipeline run completed successfully for ${env.PROJECT_NAME} by ${env.STUDENT_NAME}."
            }
        }
    }

    post {
        success {
            echo "SUCCESS: Jenkins CI/CD Pipeline completed successfully."
        }
        failure {
            echo "FAILURE: Build or test suite failed. Please inspect console output."
        }
    }
}
