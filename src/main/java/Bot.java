import org.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.client.utils.URIBuilder;
import java.net.URLEncoder;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;


import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class Bot extends TelegramLongPollingBot {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());

        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        //sendMessage.setReplyToMessageId(message.getMessageId()); отвечает на сообение
        sendMessage.setText(text);
        try {
            //setButtons(sendMessage);
            execute(sendMessage);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        Message message=update.getMessage();


        if (message != null && message.hasText()){

            if (message.getText().equals("/help")){
                sendMsg(message, "Чем могу помочь?");
            }else
            if (message.getText().equals("/start")){
                start(message);
            }//Тест git
            else {
                sendMsg(message, yaApi(message.getText()));

            }
        }else if(update.hasCallbackQuery()){

            String call_data = update.getCallbackQuery().getData();
            if (call_data.equals("set_msg_text")) {
                setingMenu(update.getCallbackQuery());
            }
            if (call_data.equals("go_start")) {
                startres(update.getCallbackQuery());
            }

        }
    }


    public void setingMenu(CallbackQuery callbackQuery){

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
        rowInline.add(new InlineKeyboardButton().setText("Что-то ещё...").setCallbackData("...."));
        List<InlineKeyboardButton> rowInline1 = new ArrayList<InlineKeyboardButton>();
        rowInline1.add(new InlineKeyboardButton().setText("Выбрать язык").setCallbackData("set_lang_msg_text"));
        List<InlineKeyboardButton> rowInline2 = new ArrayList<InlineKeyboardButton>();
        rowInline2.add(new InlineKeyboardButton().setText("Назад  <---").setCallbackData("go_start"));
        rowsInline.add(rowInline);
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);


        EditMessageText new_Message = new EditMessageText()
                .setChatId(callbackQuery.getMessage().getChatId())
                .setMessageId(callbackQuery.getMessage().getMessageId())
                .setText("Вы в настройках! ⚙⚙️⚙️")
                .setReplyMarkup(markupInline);

        try {
            execute(new_Message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public String yaApi(String text){
        URIBuilder ub = new URIBuilder();
        ub.addParameter("key", "trnsl.1.1.20190716T105549Z.168c6992ccdf8771.0162cc0cca72a7e005132b6e6a3a16e42cf7378e");
        ub.addParameter("lang", "en");
        ub.addParameter("text", text);
        String query = "https://translate.yandex.net/api/v1.5/tr.json/translate" + ub.toString();

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(query).openConnection();

            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(25000);
            connection.setReadTimeout(25000);
            connection.connect();
            StringBuilder sb = new StringBuilder();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null){
                    sb.append(line);
                    sb.append("\n");
                }
                String endText = sb.toString();
                JSONObject jObject  = new JSONObject(endText);
                return jObject.getJSONArray("text").toString().substring(2,jObject.getJSONArray("text").toString().length()-2);
            }else {
                System.out.println("Fail: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
            }
        } catch (Throwable cause){
            cause.printStackTrace();
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return "error";
    }


    public void start(Message message){


        SendMessage sendMessage = new SendMessage()
            .setChatId(message.getChatId())
            .setText("Привет!✋ " +
                        "Я бот для перевода текста!\uD83D\uDCC4");



        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
        rowInline.add(new InlineKeyboardButton().setText("Информация").setCallbackData("info_msg_text"));
        rowInline.add(new InlineKeyboardButton().setText("Настройки").setCallbackData("set_msg_text"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        try {
            execute(sendMessage); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void startres(CallbackQuery callbackQuery){


        EditMessageText sendMessage = new EditMessageText()
                .setChatId(callbackQuery.getMessage().getChatId())
                .setMessageId(callbackQuery.getMessage().getMessageId())
                .setText("Привет!✋ " + "\n" +
                        "Я бот для перевода текста!\uD83D\uDCC4");



        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
        rowInline.add(new InlineKeyboardButton().setText("Информация").setCallbackData("info_msg_text"));
        rowInline.add(new InlineKeyboardButton().setText("Настройки").setCallbackData("set_msg_text"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);
        try {
            execute(sendMessage); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }






    public String getBotUsername() {
        return "TestDevBot";
    }

    public String getBotToken() {
        return "756023596:AAGeSqyIu2mfGtfV2KOEtVYkjx2bzSb83rg";
    }
}
