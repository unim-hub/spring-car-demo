import './App.css';
import React, { useEffect, useState } from 'react';

function VehicleApp({ carDemoConfig }) {
  const [speed, setSpeed] = useState(0);
  const [gear, setGear] = useState("PARKING");
  const [pdc, setPdc] = useState("OFF");
  const [connectionStatus, setConnectionStatus] = useState('Connecting...');

  useEffect(() => {
    const socket = new WebSocket(carDemoConfig.vehicle_ws);

    socket.onopen = () => {
      setConnectionStatus('Connected');
      console.log('WebSocket connected');
    };

    socket.onmessage = (event) => {
      try {
        console.log("Vehicle Event:" + event.data);
        const data = JSON.parse(event.data);
        switch (data.eventType) {
          case 1: {
            setSpeed(data.speed);
            break;
          }
          case 2: {
            setGear(data?.gear);
            setPdc(data?.pdc ? "ON" : "OFF");
            break;
          }
          default:
            console.log("ERROR: Invalid eventId. Supported {speed:1, gear: 2}\n event:" + data);
        };
      } catch (err) {
        console.error('Invalid JSON received:', event.data);
      }
    };

    socket.onerror = (error) => {
      console.error('WebSocket error:', error);
      setConnectionStatus('Error');
    };

    socket.onclose = () => {
      setConnectionStatus('Closed');
    };

    // Cleanup on unmount
    return () => {
      socket.close();
    };
  }, [carDemoConfig.vehicle_ws]);

  return (
    <div>
      <h1>Vehicle service</h1>
      <h3>Connection state: {connectionStatus}</h3>
      <h4>
        Speed: <span style={{ fontSize: "1.5em", fontWeight: "bold" }}>{speed} km/h</span>
      </h4>
      <h4>
        Gear: <span style={{ fontSize: "1.5em", fontWeight: "bold" }}>{gear}</span>
      </h4>
      <h4>
        PDC: <span style={{ fontSize: "1.5em", fontWeight: "bold" }}>{pdc}</span>
      </h4>
    </div>
  );
};

export default VehicleApp;
