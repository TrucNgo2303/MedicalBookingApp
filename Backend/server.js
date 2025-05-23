const express = require('express');
require('dotenv').config()
require('./src/cron/appointment.cron');
const path = require('path');
const bodyParser = require('body-parser');
const authRoute = require('./src/route/auth_route/auth_route')
const patientRoute = require('./src/route/patient_route/patient_route')
const doctorRoute = require('./src/route/doctor_route/doctor_route')
const receptionistRoute = require('./src/route/receptionist_route/receptionist_route')

const app = express();
const hostname = process.env.HOST_NAME;
const port = process.env.PORT || 8888;

app.use(bodyParser.json());
app.use(express.json());

app.use('/auth', authRoute)
app.use('/patient', patientRoute)
app.use('/doctor', doctorRoute)
app.use('/receptionist', receptionistRoute)

app.get('/', (req, res) => {
    res.send("Hello world in vietnam")
});

app.listen(port, hostname, () => {
    console.log(`Server running on http://${hostname}:${port}`);
});