cd app_db
sudo su

./mvnw package

#docker build -f src/main/docker/Dockerfile.jvm -t appdb .
sudo su -c "buildah  bud --layers --format docker -f src/main/docker/Dockerfile.jvm  -t appdb ."
#tag
#docker tag appdb registry.k8s.kstych.com/appdb
sudo su -c "buildah tag appdb registry.k8s.kstych.com/appdb"
#login
sudo su -c "buildah login -u cicd -p yb9738z registry.k8s.kstych.com"

#push

#docker push registry.k8s.kstych.com/appdb
sudo su -c "buildah --format docker push registry.k8s.kstych.com/appdb"