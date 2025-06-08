#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QDialog>
#include <librdkafka/rdkafkacpp.h>

class QGroupBox;
class QGridLayout;
class QLabel;
class QLineEdit;
class QPushButton;
class QSlider;
class QRadioButton;
class QButtonGroup;

namespace com {
namespace continental {
namespace aprojector {

    class MainWindow : public QDialog
    {
        Q_OBJECT

    public:
        MainWindow(QWidget *parent = nullptr);
        ~MainWindow();

    private slots:
        void OnConnectClicked();
        void OnSpeedChanged(int speed);
        void OnGearChanged(int gear);

    private:
        const std::string VEHICLE_TOPIC = "vehicle-service";

        const int GEAR_PARKING = 0;
        const int GEAR_DRIVE = 1;
        const int GEAR_REVERSE = 2;

        const int EVENT_TYPE_SPEED = 1;
        const int EVENT_TYPE_GEAR = 2;

        const std::string EVENT_FIELD_TYPE = "eventType";
        const std::string EVENT_FIELD_SPEED = "speed";
        const std::string EVENT_FIELD_GEAR = "gear";
        const std::string EVENT_FIELD_PDC = "pdc";

        void SendMessage(std::string strMessage);

        QGroupBox* CreateConnectPanel();
        QGroupBox* CreateSpeedPanel();
        QGroupBox* CreateGearPanel();

        std::string GearToString(int gear);

        QGridLayout *mFormLayout;

        bool mBusConnected;

        QPushButton *mBusConnection;

        QLabel *mSpeedLabel;
        QLabel *mSpeedValue;
        QSlider *mSpeedSlider;

        QLabel *mGearLabel;
        QRadioButton *mParkingGear;
        QRadioButton *mReverseGear;
        QRadioButton *mDriveGear;
        QButtonGroup *mGearGroup;

        QLabel *mPDSState;


        RdKafka::Conf* mKafkaConf;
        RdKafka::Producer* mKafkaProducer;
    };

} } }

#endif // MAINWINDOW_H
