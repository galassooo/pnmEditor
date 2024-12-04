# Base image with Java 17
FROM eclipse-temurin:17-jdk

# Install Maven
RUN apt-get update && \
    apt-get install -y maven bc tree

# Create non-root user and set up directories
RUN useradd -m -s /bin/bash testuser && \
    mkdir -p /app && \
    mkdir -p /home/testuser/.imageEditor && \
    chown -R testuser:testuser /app /home/testuser && \
    chmod -R 755 /home/testuser

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Fix permissions
RUN chown -R testuser:testuser /app && \
    chmod +x test.sh && \
    chmod -R 777 /tmp

# Add debug commands to test.sh
RUN echo "echo 'Debug info before test:'" >> test.sh && \
    echo "echo 'Current user:' && whoami" >> test.sh && \
    echo "echo 'Current directory:' && pwd" >> test.sh && \
    echo "echo 'Home directory:' && echo \$HOME" >> test.sh && \
    echo "echo 'Temp directory contents:'" >> test.sh && \
    echo "ls -la /tmp" >> test.sh && \
    echo "echo 'Home directory contents:'" >> test.sh && \
    echo "ls -la \$HOME" >> test.sh && \
    echo "echo 'Permissions on .imageEditor:'" >> test.sh && \
    echo "ls -la \$HOME/.imageEditor" >> test.sh

# Switch to non-root user
USER testuser

# Set HOME environment variable
ENV HOME=/home/testuser

# Default command
CMD ["./test.sh"]