#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QDialog>
#include <librdkafka/rdkafkacpp.h>

class QGroupBox;
class QGridLayout;
class QLabel;
class QLineEdit;
class QPushButton;

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
        void OnSpeedClicked();

    private:
        const std::string VEHICLE_TOPIC = "vehicle-service";

        const int EVENT_TYPE_SPEED = 1;

        const std::string EVENT_FIELD_TYPE = "eventType";
        const std::string EVENT_FIELD_SPEED = "speed";

        void SendMessage(std::string strMessage);

        QGroupBox* CreateConnectPanel();
        QGroupBox* CreateSpeedPanel();

        QGridLayout *mFormLayout;

        bool mBusConnected;

        QPushButton *mBusConnection;

        QLabel *mSpeedLabel;
        QLineEdit *mSpeedEdit;
        QPushButton *mSendSpeed;

        RdKafka::Conf* mKafkaConf;
        RdKafka::Producer* mKafkaProducer;
    };

} } }

#endif // MAINWINDOW_H
