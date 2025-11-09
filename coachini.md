# Coachini  "particper bal de projets edition 12"

A desktop coaching application developed as part of the PIDEV 3A course at **Esprit School of Engineering**. Coachini connects coaches, investors, event creators, and users to manage training sessions, sell products, participate in events, view offers and schedules, and handle complaints.

## Description

Coachini is an innovative JavaFX desktop application designed to centralize coaching management and facilitate interaction between various actors. Developed for the PIDEV 3A course at [Esprit School of Engineering](https://esprit.tn), the platform targets not only coaches and clients but also investors, event creators, and users. Its features include product sales, event participation, coach schedules and offers, and a dedicated complaint space. Coachini provides a responsive and collaborative interface tailored to all its users.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Contributors](#contributors)
- [License](#license)
- [Acknowledgements](#acknowledgements)

## Installation

To install Coachini locally on your machine:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/housssemm/PI_DEV.git
   cd coachini-desktop
   ```

2. **Configure the database**:
   - Ensure MySQL is installed and running.
   - Set your database credentials in the `DBConnection.java` file.

3. **Create and initialize the database**:
   - Manually create the database or use an `.sql` script.
   - Import the necessary tables and data.

4. **Run the application**:
   - With Maven:
     ```bash
      javafx:run
     ```

## Usage

Once installed, launch the application from your desktop. Main actions include:

- **Sign up / Log in**: Register or log in as a coach, investor, event creator, or user.
- **Manage sessions**: Coaches can create sessions; users can book them.
- **Sell products**: Integrated store for buying coaching-related items.
- **Join events**: Sign up for thematic events.
- **Browse**: View available offers, schedules, and products.
- **Submit complaints**: Use the dedicated complaints section.

## Features

- **Secure authentication**: Registration and login for multiple user types.
- **Session management**: Plan and book coaching sessions.
- **Product sales**: Buy coaching gear and services.
- **Events**: Participate in custom events.
- **Offer browsing**: Coaches’ schedules, services, and products.
- **Complaints**: Submit and manage user complaints.
- **Responsive interface**: Designed for desktop and laptop devices.

## Technologies Used

### Frontend

- **JavaFX**: Modern GUI development
- **FXML**: UI and logic separation
- **JavaFX CSS**: UI customization

### Backend

- **Java (OpenJDK 23)**: Main development language
- **MySQL**: Relational database
- **JDBC**: Database connectivity

### Tools

- **Maven**: Dependency and project management
- **SceneBuilder**: Visual UI design

## Contributors

The following developers contributed to Coachini:

- **Farah Ben Yedder** – User admin features  
- **Houssem Laabidi** – Event management system  
- **Maissa Maalej** – Session scheduling and planning  
- **Ghada Maalej** – Product management and sales  
- **Hamza Lassoued** – Complaint management system  
- **Baha Arbi** – Offer management and coach-user interaction

To contribute:

1. **Fork the project**: Click “Fork” on [Coachini Repository](https://github.com/your-username/coachini-desktop)
2. **Clone your fork**:
   ```bash
   git clone https://github.com/your-username/coachini-desktop.git
   cd coachini-desktop
   ```
3. **Create a branch**:
   ```bash
   git checkout -b feature/your-feature
   ```
4. **Commit your changes**:
   ```bash
   git add .
   git commit -m "Add your feature"
   ```
5. **Push and create a pull request**:
   ```bash
   git push origin feature/your-feature
   ```
   Then, open a pull request on GitHub.

## License

This project currently does not have an official license. Contact the authors for more information.

## Acknowledgements

This project was developed under the supervision of **Mrs. Soumaya Sassi** at **Esprit School of Engineering** for the PIDEV 3A course. Many thanks to our classmates and mentors for their support.
