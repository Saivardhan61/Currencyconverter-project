package GUI_prac;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyConverter {

    // Extended list of supported currencies (you can add more based on the API response)
    private static final String[] CURRENCIES = {
            "USD", "EUR", "INR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "ZAR", "MXN", "BRL", "KRW", "NZD", "RUB", "SGD", "SEK", "NOK", "DKK", "TRY", "HKD"
    };

    // Swing components
    private JFrame frame;
    private JComboBox<String> fromCurrencyCombo;
    private JComboBox<String> toCurrencyCombo;
    private JTextField amountField;
    private JLabel resultTextLabel;
    private JButton convertButton;
    private JButton clearButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CurrencyConverter().createUI());
    }

    // Create the user interface
    public void createUI() {
        frame = new JFrame("Currency Converter");
        frame.setSize(500, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);  // Center the frame

        // Main panel with custom layout (BorderLayout and GridLayout for neat organization)
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(51, 153, 255));
        JLabel headerLabel = new JLabel("Currency Converter");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Center panel for form
        JPanel centerPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        centerPanel.setBackground(Color.WHITE);

        // Labels for user interface
        JLabel fromCurrencyLabel = new JLabel("From Currency:");
        JLabel toCurrencyLabel = new JLabel("To Currency:");
        JLabel amountLabel = new JLabel("Amount:");
        JLabel resultLabel = new JLabel("Converted Amount:");

        // ComboBoxes for selecting currencies
        fromCurrencyCombo = new JComboBox<>(CURRENCIES);
        toCurrencyCombo = new JComboBox<>(CURRENCIES);

        amountField = new JTextField();
        resultTextLabel = new JLabel("0.00");
        resultTextLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultTextLabel.setForeground(new Color(0, 128, 0));  // Green for results

        // Buttons for conversion and clearing fields
        convertButton = new JButton("Convert");
        clearButton = new JButton("Clear");

        // Styling buttons
        convertButton.setBackground(new Color(0, 153, 51));  // Green
        convertButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(204, 0, 0));  // Red
        clearButton.setForeground(Color.WHITE);

        // Add action listeners
        convertButton.addActionListener(this::convertCurrency);
        clearButton.addActionListener(this::clearFields);

        // Add components to the center panel
        centerPanel.add(fromCurrencyLabel);
        centerPanel.add(fromCurrencyCombo);
        centerPanel.add(toCurrencyLabel);
        centerPanel.add(toCurrencyCombo);
        centerPanel.add(amountLabel);
        centerPanel.add(amountField);
        centerPanel.add(resultLabel);
        centerPanel.add(resultTextLabel);
        centerPanel.add(clearButton);
        centerPanel.add(convertButton);

        // Add header and center panels to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Adding main panel to the frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // Action to handle currency conversion
    private void convertCurrency(ActionEvent e) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String fromCurrency = (String) fromCurrencyCombo.getSelectedItem();
            String toCurrency = (String) toCurrencyCombo.getSelectedItem();

            // Validate user input
            if (amount <= 0) {
                JOptionPane.showMessageDialog(frame, "Please enter a positive amount.");
                return;
            }

            // Fetch conversion rate from the API
            double conversionRate = fetchConversionRate(fromCurrency, toCurrency);
            if (conversionRate == 0) {
                JOptionPane.showMessageDialog(frame, "Unable to fetch conversion rate.");
                return;
            }

            // Calculate the result and display it
            double result = amount * conversionRate;
            resultTextLabel.setText(String.format("%.2f", result));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
        }
    }

    // Action to clear all fields
    private void clearFields(ActionEvent e) {
        amountField.setText("");
        resultTextLabel.setText("0.00");
        fromCurrencyCombo.setSelectedIndex(0);
        toCurrencyCombo.setSelectedIndex(0);
    }

    // Fetch live conversion rates using a public API (e.g., ExchangeRate-API)
    private double fetchConversionRate(String fromCurrency, String toCurrency) {
        try {
            // API URL for real-time conversion rates (replace with your actual API if needed)
            String apiUrl = "https://api.exchangerate-api.com/v4/latest/" + fromCurrency;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);  // Timeout after 5 seconds
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Extract conversion rate for 'toCurrency' using string manipulation
            String responseStr = response.toString();

            // Search for the target currency's rate
            String rateKey = "\"" + toCurrency + "\":";
            int rateIndex = responseStr.indexOf(rateKey);

            if (rateIndex != -1) {
                // Find the rate value (after the colon)
                int rateStart = rateIndex + rateKey.length();
                int rateEnd = responseStr.indexOf(",", rateStart);
                if (rateEnd == -1) rateEnd = responseStr.indexOf("}", rateStart);        
                String rateStr = responseStr.substring(rateStart, rateEnd).trim();

                // Convert to double and return
                return Double.parseDouble(rateStr);
            }

            return 0;  // Return 0 if the currency is not found

        } catch (Exception e) {
            e.printStackTrace();
            return 0;  // Return 0 if an error occurs (e.g., no internet connection)
        }
    }
}

