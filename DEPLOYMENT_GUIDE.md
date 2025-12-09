# EatEasy Deployment Guide

This guide will help you deploy the EatEasy application (Backend + Frontend + Database) using free resources.

## Prerequisites
1.  **GitHub Account**: You must have this code pushed to a GitHub repository.
2.  **Aiven Account** (for Database): Sign up at [aiven.io](https://aiven.io/).
3.  **Render Account** (for Hosting): Sign up at [render.com](https://render.com/).

---

## Step 1: Set up a Free MySQL Database (Aiven)

1.  Log in to **Aiven**.
2.  Click **Create Service**.
3.  Select **MySQL**.
4.  Choose **Free Plan** (often labeled as "Free" or "Service Type: Hobbyist" -> "Cloud: DigitalOcean" -> "Plan: Free"). *Note: Availability may vary, if Aiven doesn't have a free tier available, try [TiDB Cloud](https://tidbcloud.com/) or [Clever Cloud](https://www.clever-cloud.com/).*
5.  Create the service.
6.  Once running, copy the **Service URI** (Connection String). It looks like:
    `mysql://avnadmin:password@host:port/defaultdb?ssl-mode=REQUIRED`

---

## Step 2: Deploy Backend (Render)

1.  Log in to **Render**.
2.  Click **New +** -> **Web Service**.
3.  Connect your GitHub repository.
4.  Select the `backend` directory (if it asks for "Root Directory", enter `backend`).
    *   *Note: If Render doesn't support picking the subdirectory easily for Docker, you might need to ensure the Build Context is correct.*
    *   **Better Method for Monorepo**: 
        *   Choose **Build your own** -> **Docker**.
        *   **Runtime**: Docker.
        *   **Root Directory**: `backend` (This is important!).
5.  **Name**: `eateasy-backend` (or similar).
6.  **Region**: Choose one close to you (e.g., Singapore, Frankfurt).
7.  **Instance Type**: **Free**.
8.  **Environment Variables**: Add the following:
    *   `SPRING_DATASOURCE_URL`: Paste your Database Connection URI from Step 1.
        *   *Important*: If using Aiven, change `mysql://` to `jdbc:mysql://` and ensure parameters are correct.
        *   Example default format: `jdbc:mysql://host:port/defaultdb?ssl-mode=REQUIRED`
    *   `SPRING_DATASOURCE_USERNAME`: Your DB username (e.g., `avnadmin`).
    *   `SPRING_DATASOURCE_PASSWORD`: Your DB password.
    *   `JWT_SECRET`: A long random string (at least 32 chars).
    *   `PORT`: `8080`
9.  Click **Create Web Service**.
10. Wait for the build to finish. Once valid, copy the **Backend URL** (e.g., `https://eateasy-backend.onrender.com`).

---

## Step 3: Deploy Frontend (Render)

1.  Click **New +** -> **Static Site**.
2.  Connect the same GitHub repository.
3.  **Name**: `eateasy-frontend`.
4.  **Root Directory**: `Frontend`.
5.  **Build Command**: `npm install && npm run build`
6.  **Publish Directory**: `dist/eat-easy/browser`
    *   *Verify this in your `angular.json` under `architect > build > options > outputPath` if it fails.*
7.  **Click Create Static Site**.
8.  **Configure Rewrites (Crucial for API)**:
    *   Go to **Redirects/Rewrites** tab in your new Static Site dashboard.
    *   Add a new Rewrite:
        *   **Source**: `/api/*`
        *   **Destination**: `https://your-backend-url.onrender.com/api/*` (Replace with your actual Backend URL from Step 2).
        *   **Action**: Rewrite.
    *   Add another Rewrite (for Angular Routing):
        *   **Source**: `/*`
        *   **Destination**: `/index.html`
        *   **Action**: Rewrite.
9.  Save changes.

---

## Step 4: Verification

1.  Visit your **Frontend URL** (e.g., `https://eateasy-frontend.onrender.com`).
2.  Try to Login/Signup. The request will go to `/api/...`, which Render will proxy to your Backend, which connects to your Database.

---

## Troubleshooting

-   **Backend Fails to Start**: Check the Logs. Usually it's a database connection issue. Ensure the `SPRING_DATASOURCE_URL` format is correct for Java (`jdbc:mysql://...`).
-   **Frontend "404 Not Found" on Refresh**: Ensure you added the `/*` -> `/index.html` rewrite rule.
-   **API Errors**: Check the Network tab in your browser. If you see CORS errors or 404s on API calls, check your URL Rewrite rule in Render.
