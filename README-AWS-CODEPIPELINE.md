# AWS CodePipeline deployment for resume-craft

This workspace now includes a minimal AWS deployment scaffold for a Spring Boot backend and static frontend.

## Files

- `buildspec.yml` — sample AWS CodeBuild specification
- `terraform/main.tf` — core AWS VPC, network and common resources
- `terraform/ecs.tf` — ECS, ALB and task definitions
- `terraform/rds.tf` — PostgreSQL RDS
- `terraform/cloudfront.tf` — S3 and CloudFront frontend resources
- `terraform/codepipeline.tf` — CodePipeline artifact bucket and IAM policy

## Recommended flow

1. Create the AWS infrastructure with Terraform:

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

2. Push your backend image to ECR from the build process or manually.

3. Create your CodeBuild project and CodePipeline in AWS Console or Terraform.

4. Set the backend prod profile to use environment variables from ECS or Secrets Manager.

5. Deploy frontend static assets to S3 and invalidate CloudFront.
