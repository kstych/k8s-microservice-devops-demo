cd app_log
sudo su

./mvnw package

#docker build -f src/main/docker/Dockerfile.jvm -t app_log .
sudo su -c "buildah  bud --layers --format docker -f src/main/docker/Dockerfile.jvm  -t app_log ."
#tag
#docker tag apphttp registry.k8s.kstych.com/app_log
sudo su -c "buildah tag app_log registry.k8s.kstych.com/app_log"
#login
sudo su -c "buildah login -u cicd -p yb9738z registry.k8s.kstych.com"

#push

#docker push registry.k8s.kstych.com/app_log
sudo su -c "buildah --format docker push registry.k8s.kstych.com/app_log"