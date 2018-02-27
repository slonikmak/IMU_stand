package sample;

import jssc.*;

import java.util.function.Consumer;

public class SerialReader {

    private static Consumer<String> supplier;
    private static SerialPort serialPort;

    public SerialReader(int baurate, String port){
        serialPort = new SerialPort(port);
        try {
            //Открываем порт
            serialPort.openPort();
            //Выставляем параметры
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            //Включаем аппаратное управление потоком
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
            //Устанавливаем ивент лисенер и маску
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
            //Отправляем запрос устройству
            serialPort.writeString("Get data");
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }

    }

    public void serialWriteString(String s) throws SerialPortException {
        serialPort.writeString(s);
    }

    public void setOnGetString(Consumer<String> supplier){
        this.supplier = supplier;
    }


    public static String[] getPortList(){
        return SerialPortList.getPortNames();
    }

    private static class PortReader implements SerialPortEventListener {
        private String messageBuffer;

        private PortReader(){
        }

        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    //Получаем ответ от устройства, обрабатываем данные и т.д.
                    String data = serialPort.readString(event.getEventValue());
                    //И снова отправляем запрос
                    //serialPort.writeString("Get data");
                    messageBuffer = messageBuffer+data;

                    if (data.endsWith("\n")){
                        supplier.accept(messageBuffer);
                        messageBuffer = "";
                    }



                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }



}
