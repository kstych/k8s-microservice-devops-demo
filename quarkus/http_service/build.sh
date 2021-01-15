cd app_http
sudo su

./mvnw package

#docker build -f src/main/docker/Dockerfile.jvm -t apphttp .
sudo su -c "buildah  bud --layers --format docker -f src/main/docker/Dockerfile.jvm  -t apphttp ."
#tag
#docker tag apphttp registry.k8s.kstych.com/apphttp
sudo su -c "buildah tag apphttp registry.k8s.kstych.com/apphttp"
#login
sudo su -c "buildah login -u cicd -p yb9738z registry.k8s.kstych.com"

#push

#docker push registry.k8s.kstych.com/apphttp
sudo su -c "buildah --format docker push registry.k8s.kstych.com/apphttp"
