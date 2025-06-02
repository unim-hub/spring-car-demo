import React from 'react';
import ReactDOM from 'react-dom/client';
import VehicleApp from './VehicleApp';
import MediaApp from './MediaApp';

const carDemoConfig = window.CAR_DEMO_CONFIG;

 const vehicle_app = ReactDOM.createRoot(document.getElementById('vehicle_container'));
 vehicle_app.render(<VehicleApp carDemoConfig={carDemoConfig}/>);

const media_app = ReactDOM.createRoot(document.getElementById('media_container'));
media_app.render(<MediaApp carDemoConfig={carDemoConfig}/>);
