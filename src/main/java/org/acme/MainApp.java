package org.acme;

import java.io.IOException;


import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Button.Listener;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;


@QuarkusMain
public class MainApp implements QuarkusApplication {

    static class ChatBot {
        public String respond(String message) {
            // Simple responses for demonstration
            switch (message.toLowerCase()) {
                case "hello":
                case "hi":
                    return "Hello! How can I assist you today?";
                case "how are you?":
                    return "I'm a bot, but I'm functioning as expected!";
                case "bye":
                    return "Goodbye! Have a great day!";
                default:
                    return "You said: " + message;
            }
        }
    }

    @Override
    public int run(String... args) throws Exception {
        try {
            // Initialize ChatBot
            ChatBot chatBot = new ChatBot();

            // Setup Terminal and Screen
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            Screen screen = terminalFactory.createScreen();
            screen.startScreen();

            // Create GUI
            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
            final BasicWindow window = new BasicWindow("Chat Bot");

            // Create main panel with vertical layout
            Panel mainPanel = new Panel();
            mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

            // Create a scrollable window for chat history
            TextBox chatBox = createChatHistoryTextBox();
            mainPanel.addComponent(chatBox.withBorder(Borders.singleLine("Chat")));

            Panel inputPanel = createInputPanelForUserInput(chatBox, chatBot);
            mainPanel.addComponent(inputPanel.withBorder(Borders.singleLine("Input")));

            // Add main panel to window
            window.setComponent(mainPanel);

            // Add window to GUI and start
            textGUI.addWindowAndWait(window);

            // Stop screen after GUI is closed
            screen.stopScreen();
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

    private static TextBox createChatHistoryTextBox() {
        TextBox chatBox = new TextBox(new TerminalSize(80, 25), TextBox.Style.MULTI_LINE);
        chatBox.setReadOnly(true);
        chatBox.setText("Chat Bot Initialized.\n");
        return chatBox;
    }

    private Panel createInputPanelForUserInput(TextBox chatBox, ChatBot chatBot) {
        Panel inputPanel = new Panel();
        inputPanel.setLayoutManager(new GridLayout(2));

        Label inputLabel = new Label("You: ");
        TextBox inputBox = new TextBox().setPreferredSize(new TerminalSize(60, 1));
        Button sendButton = createSendButton(inputBox, chatBox, chatBot);

        inputPanel.addComponent(inputLabel);
        inputPanel.addComponent(inputBox);
        inputPanel.addComponent(new EmptySpace(new TerminalSize(0,0)));
        inputPanel.addComponent(sendButton);

        return inputPanel;
    }

    private Button createSendButton(TextBox inputBox, TextBox chatBox, ChatBot chatBot) {
        Button sendButton = new Button("Send");

        sendButton.addListener(button -> {
            String userInput = inputBox.getText().trim();
            if (!userInput.isEmpty()) {
                chatBox.setText(chatBox.getText() + "You: " + userInput + "\n");
                String botResponse = chatBot.respond(userInput);
                chatBox.setText(chatBox.getText() + "Bot: " + botResponse + "\n");
                inputBox.setText("");
            }
        });
        return sendButton;
    }
}
