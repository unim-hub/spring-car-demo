#include "MainWindow.h"
#include "Log.h"

#include <QGridLayout>
#include <QGroupBox>
#include <QLabel>
#include <QLineEdit>
#include <QPushButton>
#include <QThread>
#include <QSlider>
#include <QRadioButton>
#include <QButtonGroup>

namespace com {
namespace continental {
namespace aprojector {

MainWindow::MainWindow(QWidget *parent)
    : QDialog(parent),
    mFormLayout(nullptr),
    mBusConnected(false),
    mBusConnection(nullptr),
    mSpeedLabel(nullptr),
    mSpeedValue(nullptr),
    mSpeedSlider(nullptr),
    mGearLabel(nullptr),
    mParkingGear(nullptr),
    mReverseGear(nullptr),
    mDriveGear(nullptr),
    mGearGroup(nullptr),
    mPDSState(nullptr),
    mKafkaConf(nullptr),
    mKafkaProducer(nullptr)
{
    LOGI << std::endl;

    mBusConnection = new QPushButton(tr("Connect"), parent);
    mBusConnection->setFixedWidth(100);
    QObject::connect(mBusConnection, &QPushButton::clicked, this, &MainWindow::OnConnectClicked);

    mSpeedLabel = new QLabel(tr("Speed: "), parent);
    mSpeedLabel->setFixedWidth(50);

    mSpeedValue = new QLabel("0 km/h", parent);
    mSpeedValue->setFixedWidth(100);

    mSpeedSlider = new QSlider(Qt::Horizontal, parent);
    mSpeedSlider->setFixedWidth(150);
    mSpeedSlider->setRange(0, 300);
    mSpeedSlider->setTickInterval(1);
    mSpeedSlider->setEnabled(false);
    QObject::connect(mSpeedSlider, &QSlider::valueChanged, this, &MainWindow::OnSpeedChanged);

    mGearLabel = new QLabel("Gear:", parent);

    mParkingGear = new QRadioButton("Parking", parent);
    mParkingGear->setFixedWidth(100);
    mReverseGear = new QRadioButton("Reverse", parent);
    mReverseGear->setFixedWidth(100);
    mDriveGear = new QRadioButton("Drive", parent);
    mDriveGear->setFixedWidth(100);

    mGearGroup = new QButtonGroup(parent);
    mGearGroup->addButton(mParkingGear, GEAR_PARKING);
    mGearGroup->addButton(mReverseGear, GEAR_REVERSE);
    mGearGroup->addButton(mDriveGear, GEAR_DRIVE);
    mParkingGear->setChecked(true);
    QObject::connect(mGearGroup, &QButtonGroup::idClicked, this, &MainWindow::OnGearChanged);

    mPDSState = new QLabel("PDC: OFF", parent);

    mFormLayout = new QGridLayout(this);
    mFormLayout->setSpacing(1);
    mFormLayout->setMargin(1);

    mFormLayout->addWidget(CreateConnectPanel(), 0, 0);
    mFormLayout->addWidget(CreateSpeedPanel(), 1, 0);
    mFormLayout->addWidget(CreateGearPanel(), 2, 0);

    setLayout(mFormLayout);
}

MainWindow::~MainWindow()
{

}

void MainWindow::OnConnectClicked()
{
    if (!mBusConnected)
    {
        // Kafka configuration
        std::string errstr;
        mKafkaConf = RdKafka::Conf::create(RdKafka::Conf::CONF_GLOBAL);
        mKafkaConf->set("bootstrap.servers", "localhost:9092", errstr);

        // Create producer
        mKafkaProducer = RdKafka::Producer::create(mKafkaConf, errstr);
        if (mKafkaProducer)
        {
            mBusConnected = true;
            mSpeedSlider->setEnabled(true);
            mBusConnection->setText(tr("Disconnect"));
        }
        else
        {
            LOGE << "Creating Kafka producer fails" << std::endl;
        }
    }
    else
    {
        mSpeedSlider->setEnabled(false);
        mBusConnection->setText(tr("Connect"));

        delete mKafkaProducer;
        mKafkaProducer = nullptr;
        delete mKafkaConf;
        mKafkaConf = nullptr;
        mBusConnected = false;
    }
}

void MainWindow::OnSpeedChanged(int speed)
{
    mSpeedValue->setText(QString("%1 km/h").arg(speed));
    if (mKafkaProducer)
    {
        QString message = QString("{\"%1\":%2,\"%3\":%4}")
                              .arg(EVENT_FIELD_TYPE.c_str()).arg(EVENT_TYPE_SPEED)
                              .arg(EVENT_FIELD_SPEED.c_str()).arg(speed);
        std::string strMessage = message.toStdString();
        SendMessage(strMessage); // Wait for delivery
    }
}

void MainWindow::OnGearChanged(int gear)
{

    mSpeedSlider->setValue(gear != GEAR_DRIVE ? 0 : mSpeedSlider->value());
    mSpeedSlider->setEnabled(gear == GEAR_DRIVE);
    mPDSState->setText(QString("PDC: %1").arg(gear == GEAR_REVERSE ? "ON" : "OFF"));

    if (mKafkaProducer)
    {
        QString message = QString("{\"%1\":%2,\"%3\":\"%4\",\"%5\":%6}")
                              .arg(EVENT_FIELD_TYPE.c_str()).arg(EVENT_TYPE_GEAR)
                              .arg(EVENT_FIELD_GEAR.c_str()).arg(GearToString(gear).c_str())
                              .arg(EVENT_FIELD_PDC.c_str()).arg(gear == GEAR_REVERSE ? "true" : "false");
        std::string strMessage = message.toStdString();
        SendMessage(strMessage); // Wait for delivery
    }
}

void MainWindow::SendMessage(std::string strMessage)
{
    RdKafka::ErrorCode resp = mKafkaProducer->produce(
        VEHICLE_TOPIC, RdKafka::Topic::PARTITION_UA, RdKafka::Producer::RK_MSG_COPY,
        const_cast<char*>(strMessage.c_str()), strMessage.size(),
        nullptr, 0, 0, nullptr, nullptr);

    if (resp != RdKafka::ERR_NO_ERROR)
    {
        LOGE << "Produce failed: " << RdKafka::err2str(resp) << std::endl;
    }

    mKafkaProducer->flush(1000);
}

QGroupBox* MainWindow::CreateConnectPanel()
{
    QGroupBox *gb = new QGroupBox;

    QHBoxLayout *layout = new QHBoxLayout;
    layout->addWidget(mBusConnection);
    layout->addStretch(1);
    layout->setAlignment(Qt::AlignCenter);
    gb->setLayout(layout);
    return gb;
}

QGroupBox* MainWindow::CreateSpeedPanel()
{
    QGroupBox *gb = new QGroupBox;

    QHBoxLayout *layout = new QHBoxLayout;
    layout->addWidget(mSpeedLabel);
    layout->addWidget(mSpeedValue);
    layout->addWidget(mSpeedSlider);
    layout->addStretch(1);
    layout->setAlignment(Qt::AlignLeft);
    gb->setLayout(layout);
    return gb;
}

QGroupBox *MainWindow::CreateGearPanel()
{
    QGroupBox *gb = new QGroupBox;

    QHBoxLayout *layout = new QHBoxLayout;
    layout->addWidget(mGearLabel);

    QGroupBox *gb2 = new QGroupBox;
    QVBoxLayout *layout2 = new QVBoxLayout;
    layout2->addWidget(mParkingGear);
    layout2->addWidget(mDriveGear);
    layout2->addWidget(mReverseGear);
    layout2->addStretch(1);
    layout2->setAlignment(Qt::AlignLeft);
    gb2->setLayout(layout2);

    layout->addWidget(gb2);
    layout->addWidget(mPDSState);
    layout->addStretch(1);
    layout->setAlignment(Qt::AlignLeft);
    gb->setLayout(layout);
    return gb;
}

std::string MainWindow::GearToString(int gear)
{
    if (gear == GEAR_DRIVE)
    {
        return "DRIVE";
    }
    else if (gear == GEAR_REVERSE)
    {
        return "REVERSE";
    }
    else
    {
        return "PARKING";
    }
}

}}}
