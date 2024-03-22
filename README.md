Ok, so I wanted a simple bot that would take opsgenie on call shifts and update slack topics with them.
But I also wanted to play around with AI/LLM's, since this feels like a task well suited for them. 

Results are mixed. Some things worked well-ish. Others didn't. By and large, nothing worked straight up, and all required rework.
If I didn't have some engineering skills, I probably couldn't have made this work. As it is, it helped speed on API exploration, 
but small issues like incorrect spring boot usage cost me time. Overall, I'd call it a wash. I specifically also tried to focus 
on using the AI stuff for scaffolding, but then also to write test code. Similar to above, it was meh. I tried Claude and ChatGPT, and for the tests
I separated them out. Not all are running, but Claude was a step better than chatgpt. Anyways, this is the claude generated README for this. Apparently I've chosen the MIT license!

# Panic Button

Panic Button is a Kotlin-based application that integrates with OpsGenie and Slack to provide on-call management and notification capabilities. It allows you to fetch on-call teams from OpsGenie, post on-call messages to Slack channels, and update Slack user groups with the current on-call users.

## Features

- Fetches on-call teams and users from OpsGenie API
- Posts on-call messages to designated Slack channels
- Updates Slack user groups with the current on-call users
- Scheduled job to periodically sync on-call information
- Dockerized application for easy deployment

## Prerequisites

- Java 11 or higher
- Gradle
- Docker
- OpsGenie API key
- Slack Bot token and channel details

## Configuration

1. Update the `application.yml` file with the necessary configurations:
    - OpsGenie API key
    - Slack Bot token
    - Slack channel mappings
    - Slack user group mappings

2. Set up the following secrets in your GitHub repository's settings:
    - `DOCKER_USERNAME`: Your Docker Hub username
    - `DOCKER_PASSWORD`: Your Docker Hub password
    - `GITHUB_TOKEN`: A GitHub token with permissions to create releases

## Building and Running

To build and run the Panic Button application locally, follow these steps:

1. Clone the repository:
   ```
   git clone https://github.com/your-username/panic-button.git
   ```

2. Build the application using Gradle:
   ```
   ./gradlew build
   ```

3. Run the tests:
   ```
   ./gradlew test
   ```

4. Build the Docker image:
   ```
   docker build -t panic-button .
   ```

5. Run the Docker container:
   ```
   docker run -p 8080:8080 panic-button
   ```

The application will start running on `http://localhost:8080`.

## Deployment

The Panic Button application is automatically built, tested, and deployed using GitHub Actions. The workflow is defined in `.github/workflows/build-test-deploy.yml`.

Whenever changes are pushed or pulled to the `main` branch, the workflow will:
1. Build the application using Gradle
2. Run the tests
3. Create a Docker image
4. Push the Docker image to a public repository
5. Create a release in GitHub with the corresponding tag

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).
