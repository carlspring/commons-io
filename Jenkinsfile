def REPO_NAME = 'carlspring/commons-io'
def DEPLOY = false
def SERVER_ID = 'carlspring-oss-snapshots'
def SERVER_URL = 'https://dev.carlspring.org/nexus/content/repositories/carlspring-oss-snapshots/'
def IS_STABLE = false

pipeline {
    agent {
        node {
            label 'master'
        }
    }
    stages {
        stage('Build') {
            steps {
                withMaven(maven: 'maven-3.3.9',
                          mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833',
                          mavenLocalRepo: '/home/jenkins/.m2/repository')
                {
                    sh 'mvn -U clean install -Dmaven.test.failure.ignore=true'
                }
            }
            post {
                success {
                    script {
                        IS_STABLE = true
                    }
                }
            }
        }
        stage('Code Analysis') {
            steps {
                withMaven(maven: 'maven-3.3.9',
                          mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833',
                          mavenLocalRepo: '/home/jenkins/.m2/repository')
                {
                    script {
                        if(env.BRANCH_NAME == 'master') {
                            withSonarQubeEnv('sonar') {
                                // requires SonarQube Scanner for Maven 3.2+
                                sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar"
                                DEPLOY = true;
                            }
                        }
                        else {
                            if(env.BRANCH_NAME.startsWith("PR-"))
                            {
                                withSonarQubeEnv('sonar') {
                                    def PR_NUMBER = env.CHANGE_ID
                                    echo "Triggering sonar analysis in comment-only mode for PR: ${PR_NUMBER}."
                                    sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar " +
                                       " -Psonar-github" +
                                       " -Dsonar.github.repository=${REPO_NAME}" +
                                       " -Dsonar.github.pullRequest=${PR_NUMBER}"
                                }
                            }
                            else
                            {
                                echo "This step is skipped for branches other than master or PR-*"
                            }
                        }
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    if(DEPLOY && IS_STABLE) {
                        withMaven(maven: 'maven-3.3.9',
                                  mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833',
                                  mavenLocalRepo: '/home/jenkins/.m2/repository')
                        {
                            sh "mvn package "+
                               " -Dmaven.test.skip=true" +
                               " deploy:deploy" +
                                " -DaltDeploymentRepository=${SERVER_ID}::default::${SERVER_URL}"
                        }
                    } else {
                        echo 'This step is skipped when triggered from branch different than master or if there are failing test cases.'
                    }
                }
            }
        }
    }
    post {
        always {
            deleteDir()
        }
    }
}
