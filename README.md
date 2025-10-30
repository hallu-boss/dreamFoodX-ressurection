# Installation

## Data base

```
docker compose up --build
```

## Backend

go to `backend`

install dependencies:
```
npm install
```

connect shema with database:
```
npx prisma migrate dev
```

seed database:
```
node run seed
```

### Run server
```
node server.js
```

## Frontend

go to `frontend`

install dependencies:
```
npm install
```

### Run frontend

```
npm run dev
```
