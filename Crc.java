import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Crc extends JFrame {
    private JPanel cont = new JPanel();
    // Bouton coder
    private JButton coder = new JButton("Coder");
    // Bouton Quitter
    private JButton quitter = new JButton("Quitter");
    // Bouton Verifier
    private JButton verifier = new JButton("Verifier");
    private JLabel txt = new JLabel("Entrer le code et le diviseur");
    private JPanel cont2 = new JPanel();
    // Champs de texte
    private JTextField code = new JTextField(30);
    private JTextField div = new JTextField(10);
    private String txtcontent;
    private String diviseur;
    private DrawingPanel drawingPanel = new DrawingPanel();

    public Crc() {
        add(cont, BorderLayout.SOUTH);
        add(cont2, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        cont2.add(txt);
        cont2.add(code);
        cont2.add(div);
        cont.add(coder);
        cont.add(verifier);
        cont.add(quitter);
        quitter.addActionListener(new MyListener());
        coder.addActionListener(new MyListener());
        verifier.addActionListener(new MyListener());
    }
    /**
     * Fonction qui permet de verfier si la chaine entré est binaire 
     * c-a-d elle est constituée de 0 et 1.
     * @param s
     * @return si la chaine entrer est binaire ou pas.
     */
    private boolean binaire(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '1') {
                return true;
            }
        }
        return false;
    }
    /**
     * Cette méthode verifie si le Crc du message est valide ou non
     * @param input
     * @param polynomeGenerateur
     * @return true si le Crc est valide, false sinon
     */
    public boolean isCrcEstValide(String input, String polynomeGenerateur) {
        StringBuilder messageBuilder = new StringBuilder(input);
        int paddingLength = polynomeGenerateur.length() - 1;
        messageBuilder.append("0".repeat(paddingLength));
        String message = messageBuilder.toString();

        StringBuilder remainderBuilder = new StringBuilder(message);

        int generatorLength = polynomeGenerateur.length();
        int endIndex = message.length() - generatorLength;

        for (int i = 0; i <= endIndex; i++) {
            if (remainderBuilder.charAt(i) == '0') {
                continue;
            }

            for (int j = 0; j < generatorLength; j++) {
                char messageBit = remainderBuilder.charAt(i + j);
                char generatorBit = polynomeGenerateur.charAt(j);

                remainderBuilder.setCharAt(i + j, xor(messageBit, generatorBit));
            }
        }

        return remainderBuilder.substring(endIndex + 1).equals("0".repeat(generatorLength - 1));
    }
    /**
     * Fonction qui permet de faire la division du message entré par le polynome generateur.
     * @param inputMessage
     * @param generateur
     * @return le reste de la division.
     */
    private String calculeAvecEtape(String inputMessage, String generateur) {
        StringBuilder messageBuilder = new StringBuilder(inputMessage);
        int paddingLength = generateur.length() - 1;
        messageBuilder.append("0".repeat(paddingLength));
        String message = messageBuilder.toString();

        StringBuilder remainderBuilder = new StringBuilder(message);

        int generateurLength = generateur.length();
        int finIndex = message.length() - generateurLength;

        StringBuilder steps = new StringBuilder();

        for (int i = 0; i <= finIndex; i++) {
            if (remainderBuilder.charAt(i) == '0') {
                continue;
            }

            steps.append("Etape ").append(i + 1).append(": \n");
            steps.append("   Message actuel: ").append(remainderBuilder.toString()).append("\n");

            for (int j = 0; j < generateurLength; j++) {
                char messageBit = remainderBuilder.charAt(i + j);
                char generateurBit = generateur.charAt(j);

                remainderBuilder.setCharAt(i + j, xor(messageBit, generateurBit));
            }

            steps.append("   XOR avec le generateur: ").append(generateur).append("\n");
            steps.append("   Resultat: ").append(remainderBuilder.toString()).append("\n");
        }

        steps.append("\nResultat final: ").append(remainderBuilder.substring(finIndex + 1));

        // Afficher les étapes de calcul dans une boîte de dialogue
        JOptionPane.showMessageDialog(Crc.this, steps.toString(), "Etapes du Calcul CRC", JOptionPane.INFORMATION_MESSAGE);

        return remainderBuilder.substring(finIndex + 1);
    }
    /**
     * Méthode qui permet de faire la division
     * si on a deux bits identique donc cela sera un 0
     * si on a deux bits differents donc cela sera un 1.
     * @param a
     * @param b
     * @return 1 ou 0 selon les deux bits.
     */
    private char xor(char a, char b) {
        return (a == b) ? '0' : '1';
    }
    /*Classe MyListener qui permet d'implementer actionperformed 
     * qui va effectuer des actions selon le boutons cliqué.
     */
    private class MyListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            String tape = ae.getActionCommand();
            if ("Quitter".equals(tape)) {
                System.exit(0);
            } else if ("Coder".equals(tape)) {
                coder();
                VerifierCRC();
            } else if ("Verifier".equals(tape)) {
                VerifierCRC();
            }
        }
    }
    /**
     * Methode qui permet de verifier si le CRC genere est correct ou non
     * et afficher le resultat dans une boîte de dialogue
     */
    private void VerifierCRC() {
        txtcontent = code.getText();
        diviseur = div.getText();

        if (!binaire(txtcontent) && !binaire(diviseur)) {
            // Vérifier si le CRC généré est correct
            boolean isCorrect = isCrcEstValide(txtcontent, diviseur);
            if (isCorrect) {
                JOptionPane.showMessageDialog(Crc.this, "CRC Correcte", "Verification CRC", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(Crc.this, "CRC Incorrecte", "Verification CRC", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(Crc.this, "Les donnees ne sont pas binaires", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Méthode qui permet d'effectuer la division du message entré par le polynome generateur.
     */
    private void coder() {
        txtcontent = code.getText();
        diviseur = div.getText();

        if (!binaire(txtcontent) && !binaire(diviseur)) {
            // Utiliser la méthode calculateWithSteps pour obtenir le CRC avec les étapes
            String crc = calculeAvecEtape(txtcontent, diviseur);

            // Afficher le message original avec le CRC
            JOptionPane.showMessageDialog(Crc.this, "Message original avec CRC : " + txtcontent + crc, "Message avec CRC", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(Crc.this, "Les donnees ne sont pas binaires", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Crc());
    }
}
