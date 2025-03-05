rootProject.name = "DemoProject"
include("src:main:proto")
findProject(":src:main:proto")?.name = "proto"
