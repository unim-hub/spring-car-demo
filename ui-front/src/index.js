import React from 'react';
import ReactDOM from 'react-dom/client';
import VehicleApp from './VehicleApp';

const carDemoConfig = window.CAR_DEMO_CONFIG;

const vehicle_app = ReactDOM.createRoot(document.getElementById('vehicle_container'));
vehicle_app.render(<VehicleApp carDemoConfig={carDemoConfig}/>);

