# Build
custom_build(
   # Name of the container image
   ref = 'catalog-service',
   # command to build the container image
   command = './gradlew bootBuildImage --imageName $EXPECTED_REF',
   # Files to watch that trigger a new build
   deps = ['build.gradle', 'src']
)

k8s_yaml(kustomize('k8s')

# Deploy
k8s_yaml(['k8s/deployment.yml', 'k8s/service.yml'])

# Manage
k8s_resource('catalog-service', port_forwards=['8081'])