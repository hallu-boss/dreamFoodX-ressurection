# Backend Dockerfile - Debian-based
FROM node:18-slim

WORKDIR /app

# Install OpenSSL and other dependencies
RUN apt-get update -y && apt-get install -y openssl

# Copy package files
COPY package*.json ./
COPY prisma ./prisma/

# Install dependencies
RUN npm install

COPY src ./src/
COPY tsconfig.json ./tsconfig.json

# Copy .env file if it exists
COPY .env* ./

RUN npx prisma generate

# Build TypeScript
RUN npm run build

EXPOSE 5000
EXPOSE 5555

CMD ["npm", "start"]
