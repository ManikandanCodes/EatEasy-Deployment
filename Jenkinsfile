pipeline {
    agent any

    /* ------------------------------------------------------------
       INSTALL REQUIRED TOOLS FOR BUILDING ANGULAR + SPRING BOOT
       ------------------------------------------------------------ */
    tools { 
        nodejs "NodeJS"        // Required for Angular
        jdk "JDK17"            // Java backend
        maven "Maven"          // Builds Spring Boot JAR
    }

    /* ------------------------------------------------------------
       GLOBAL VARIABLES USED THROUGHOUT THE PIPELINE
       ------------------------------------------------------------ */
    environment {
        FRONTEND_DIR = "Frontend"
        BACKEND_DIR = "backend"
        STACK_FILE = "docker-stack.yml"
        STACK_NAME = "eateasy"
    }

    stages {

        /* ------------------------------------------------------------
           STAGE 1: CLEAN WORKSPACE + DOWNLOAD LATEST CODE FROM GITHUB
           ------------------------------------------------------------ */
        stage('Checkout') {
            steps {
                cleanWs()  // Prevents old build artifacts
                // UPDATED: Pointing to the Deployment repo where docker-stack.yml exists
                git branch: 'main', url: 'https://github.com/ManikandanCodes/EatEasy-Deployment.git'
            }
        }

        /* ------------------------------------------------------------
           STAGE 2: INSTALL ANGULAR DEPENDENCIES + PRODUCTION BUILD
           ------------------------------------------------------------ */
        stage('Build Frontend') {
            steps {
                dir("${FRONTEND_DIR}") {
                    bat 'npm ci'                 // Clean installation
                    bat 'npm run build --prod'   // Build Angular dist folder
                }
            }
        }

        /* ------------------------------------------------------------
           STAGE 3: BUILD SPRING BOOT BACKEND JAR
           ------------------------------------------------------------ */
        stage('Build Backend') {
            steps {
                dir("${BACKEND_DIR}") {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        /* ------------------------------------------------------------
           STAGE 4: BUILD DOCKER IMAGES FOR FRONTEND + BACKEND
           ------------------------------------------------------------ */
        stage('Build Images') {
            steps {
                bat "docker build -t eateasy-backend:latest ${BACKEND_DIR}"
                bat "docker build -t eateasy-frontend:latest ${FRONTEND_DIR}"
            }
        }

        /* ------------------------------------------------------------
           STAGE 5: ENSURE SWARM MODE IS ENABLED
           ------------------------------------------------------------ */
        stage('Init Swarm') {
            steps {
                bat '''
                docker info | findstr /C:"Swarm: active" >nul
                if %ERRORLEVEL% NEQ 0 (
                    echo Swarm inactive — initializing...
                    docker swarm init
                ) else (
                    echo Docker Swarm already active.
                )
                '''
            }
        }

        /* ------------------------------------------------------------
           STAGE 6: CREATE GLOBAL OVERLAY NETWORK (IF NOT EXISTS)
           ------------------------------------------------------------ */
        stage('Create Overlay Network') {
            steps {
                bat '''
                docker network ls | findstr eat-easy-network >nul
                if %ERRORLEVEL% NEQ 0 (
                    echo Creating overlay network...
                    docker network create --driver overlay eat-easy-network
                ) else (
                    echo Overlay network already exists.
                )
                '''
            }
        }

        /* ------------------------------------------------------------
           STAGE 7: DEPLOY THE DOCKER SWARM STACK
           FIX → USE FULL PATH TO docker-stack.yml
           ------------------------------------------------------------ */
        stage('Deploy Stack') {
            steps {
                bat """
                echo Workspace files:
                dir %WORKSPACE%

                echo Deploying Docker Swarm Stack: ${STACK_NAME}
                docker stack deploy -c "%WORKSPACE%\\${STACK_FILE}" ${STACK_NAME}

                echo Waiting for services...
                timeout /t 5 >nul

                echo Services running:
                docker service ls
                """
            }
        }
    }

    /* ------------------------------------------------------------
       FINAL MESSAGE AFTER BUILD COMPLETION
       ------------------------------------------------------------ */
    post {
        success {
            echo 'SUCCESS — EatEasy Docker Swarm Deployment Completed!'
        }
        failure {
            echo 'FAILED — Check Jenkins logs for issues.'
        }
    }
}
