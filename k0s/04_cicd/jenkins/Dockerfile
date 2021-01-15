#buildah bud --layers --format docker -t jenkinscicd .
#buildah tag jenkinscicd registry.k8s.kstych.com/cicd/jenkinscicd
#buildah --format docker push registry.k8s.kstych.com/cicd/jenkinscicd

FROM fedora:latest

RUN dnf install -y wget
RUN wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
RUN rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key
RUN dnf upgrade -y
RUN dnf install -y jenkins java-devel git
RUN dnf install -y podman buildah maven fuse-overlayfs --exclude container-selinux ; rm -rf /var/cache /var/log/dnf* /var/log/yum.*


RUN sed -i -e 's|^#mount_program|mount_program|g' -e '/additionalimage.*/a "/var/lib/shared",' /etc/containers/storage.conf
RUN usermod -a -G wheel jenkins
RUN sed --in-place 's/^#\s*\(%wheel\s\+ALL=(ALL)\s\+NOPASSWD:\s\+ALL\)/\1/' /etc/sudoers
RUN mkdir -p /var/lib/shared/overlay-images /var/lib/shared/overlay-layers; touch /var/lib/shared/overlay-images/images.lock; touch /var/lib/shared/overlay-layers/layers.lock


RUN touch /etc/subgid /etc/subuid \
 && chmod g=u /etc/subgid /etc/subuid /etc/passwd \
 && echo jenkins:10000:65536 > /etc/subuid \
 && echo jenkins:10000:65536 > /etc/subgid

RUN echo "export BUILDAH_ISOLATION=chroot" >> /var/lib/jenkins/.bashrc
RUN mkdir -p /var/lib/jenkins/.config/containers && echo "driver=\"vfs\"" > /var/lib/jenkins/.config/containers/storage.conf

RUN dnf install -y moby-engine

USER jenkins
WORKDIR /var/lib/jenkins

CMD ["java", "-jar", "/usr/lib/jenkins/jenkins.war","--httpPort=8080"]

# if USER=JENKINS homefolder = /var/lib/jenkins/
# if USER=root homefolder = /root/.jenkins/


#podman run -it --rm --device /dev/fuse -p8080:8080 -v/.../home:/var/lib/jenkins:Z -v/.../:/var/lib/containers:Z --name=jenkins jenkinstest
