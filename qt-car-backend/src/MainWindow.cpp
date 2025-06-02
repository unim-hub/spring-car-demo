#include "MainWindow.h"
#include "Log.h"

#include <QGridLayout>
#include <QGroupBox>
#include <QLabel>
#include <QLineEdit>
#include <QPushButton>
#include <QThread>

namespace com {
namespace continental {
namespace aprojector {

MainWindow::MainWindow(QWidget *parent)
    : QDialog(parent),
    mFormLayout(nullptr),
    mBusConnected(false),
    mBusConnection(nullptr),
    mSpeedLabel(nullptr),
    mSpeedEdit(nullptr),
    mSendSpeed(nullptr),
    mKafkaConf(nullptr),
    mKafkaProducer(nullptr)
{
    LOGI << std::endl;

    mBusConnection = new QPushButton(tr("Connect"), parent);
    mBusConnection->setFixedWidth(100);
    QObject::connect(mBusConnection, &QPushButton::clicked, this, &MainWindow::OnConnectClicked);

    mSpeedLabel = new QLabel(tr("Speed: "), parent);
    mSpeedLabel->setFixedWidth(150);

    mSpeedEdit = new QLineEdit("0", parent);
    mSpeedEdit->setFixedWidth(50);

    mSendSpeed = new QPushButton(tr("Send Speed"), parent);
    mSendSpeed->setFixedWidth(100);
    QObject::connect(mSendSpeed, &QPushButton::clicked, this, &MainWindow::OnSpeedClicked);

    mFormLayout = new QGridLayout(this);
    mFormLayout->setSpacing(1);
    mFormLayout->setMargin(1);

    mFormLayout->addWidget(CreateConnectPanel(), 0, 0);
    mFormLayout->addWidget(CreateSpeedPanel(), 1, 0);

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
            mSendSpeed->setEnabled(true);
            mSpeedEdit->setEnabled(true);
            mBusConnection->setText(tr("Disconnect"));
        }
        else
        {
            LOGE << "Creating Kafka producer fails" << std::endl;
        }
    }
    else
    {
        mSendSpeed->setEnabled(false);
        mSpeedEdit->setEnabled(false);
        mBusConnection->setText(tr("Connect"));

        delete mKafkaProducer;
        mKafkaProducer = nullptr;
        delete mKafkaConf;
        mKafkaConf = nullptr;
        mBusConnected = false;
    }
}

void MainWindow::OnSpeedClicked()
{
    if (mKafkaProducer)
    {
        QString strMinPort = mSpeedEdit->text();
        bool bOk = false;
        int speed = strMinPort.toUInt(&bOk, 10);
        if (bOk)
        {
            QString message = QString("{\"%1\":%2,\"%3\":%4}").arg(EVENT_FIELD_TYPE.c_str()).arg(EVENT_TYPE_SPEED).arg(EVENT_FIELD_SPEED.c_str()).arg(speed);
            std::string strMessage = message.toStdString();
            SendMessage(strMessage); // Wait for delivery
        }
        else
        {
            //Display error
        }
    }
}

void MainWindow::SendMessage(std::string strMessage)
{
    RdKafka::ErrorCode resp = mKafkaProducer->produce(
        VEHICLE_TOPIC, RdKafka::Topic::PARTITION_UA, RdKafka::Producer::RK_MSG_COPY,
        const_cast<char*>(strMessage.c_str()), strMessage.size(),
        nullptr, 0, 0, nullptr, nullptr);

    if (resp != RdKafka::ERR_NO_ERROR)
        LOGE << "Produce failed: " << RdKafka::err2str(resp) << std::endl;
    else
        LOGI << "Message produced." << std::endl;

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
    layout->addWidget(mSpeedEdit);
    layout->addWidget(mSendSpeed);
    layout->addStretch(1);
    layout->setAlignment(Qt::AlignLeft);
    gb->setLayout(layout);
    return gb;
}

}}}
