package kz.greetgo.sandbox.stand.launchers;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.util.Modules;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Vector;

import static kz.greetgo.sandbox.controller.model.PhoneType.MOBILE;

public class LaunchGenerateMockObjects {
    public static void main(String argv[]) throws Exception {
        new LaunchGenerateMockObjects().run();
    }

    public final static String CLIENTS = "StandDbClients.txt";
    public final static String ADDRESSES = "StandDbAddresses.txt";
    public final static String PHONES = "StandDbPhones.txt";
    public final static String ACCOUNTS = "StandDbAccounts.txt";
    public final static String TRANSACTIONS = "StandDbTransactions.txt";
    public final static String TRANSACTION_TYPES = "StandDbTransactionTypes.txt";
    public final static String CHARMS = "StandDbCharms.txt";

    private class Printer {
        Vector<Client> clients = new Vector<>();
        Vector<ClientAddress> clientAddresses = new Vector<>();
        Vector<ClientPhone> clientPhones = new Vector<>();
        Vector<ClientAccount> clientAccounts = new Vector<>();
        Vector<ClientAccountTransaction> clientAccountTransactions = new Vector<>();
        Vector<TransactionType> transactionTypes = new Vector<>();
        Vector<Charm> charms = new Vector<>();
        File srcDir;

        public Printer setClient(Client c) {
            this.clients.add(c);
            return this;
        }

        public Printer setClientAddress(ClientAddress c) {
            this.clientAddresses.add(c);
            return this;
        }

        public Printer setClientPhone(ClientPhone phone) {
            this.clientPhones.add(phone);
            return this;
        }

        public Printer setClientAccount(ClientAccount account) {
            this.clientAccounts.add(account);
            return this;
        }

        public Printer setClientAccountTransaction(ClientAccountTransaction transaction) {
            this.clientAccountTransactions.add(transaction);
            return this;
        }

        public Printer setTransactionType(TransactionType type) {
            this.transactionTypes.add(type);
            return this;
        }

        public Printer setCharm(Charm charm) {
            this.charms.add(charm);
            return this;
        }

        public Printer setSrcDir(File dir) {
            this.srcDir = dir;
            return this;
        }

        public void execute() {
            if (srcDir == null) return;

            String[] files =
                    new String[]{CLIENTS, ADDRESSES, PHONES, ACCOUNTS, TRANSACTIONS, TRANSACTION_TYPES, CHARMS};

            for (String file : files) {
                removeFile(file);
            }

            for (Client client : clients)
                printClient(client);
            for (ClientAddress clientAddress : clientAddresses)
                printClientAddress(clientAddress);
            for (ClientPhone phone : clientPhones)
                printClientPhone(phone);
            for (ClientAccount account : clientAccounts)
                printClientAccount(account);
            for (ClientAccountTransaction transaction : clientAccountTransactions)
                printClientAccountTransaction(transaction);
            for (TransactionType type : transactionTypes)
                printTransactionType(type);
            for (Charm charm : charms)
                printCharm(charm);
        }

        private void printCharm(Charm charm) {
            if (charm == null) return;
            StringBuilder sb = new StringBuilder();
            sb.append(charm.id).append(";")
                    .append(charm.name).append(";")
                    .append(charm.description).append(";")
                    .append(charm.energy).append(";");
            printFile(sb.toString(), CHARMS);
        }

        private void printTransactionType(TransactionType transactionType) {
            if (transactionType == null) return;
            StringBuilder sb = new StringBuilder();
            sb.append(transactionType.id).append(";")
                    .append(transactionType.code).append(";")
                    .append(transactionType.name).append(";");
            printFile(sb.toString(), TRANSACTION_TYPES);
        }

        private void printClientAccountTransaction(ClientAccountTransaction clientAccountTransaction) {
            if (clientAccountTransaction == null) return;
            StringBuilder sb = new StringBuilder();
            sb.append(clientAccountTransaction.id).append(";")
                    .append(clientAccountTransaction.account).append(";")
                    .append(clientAccountTransaction.money).append(";")
                    .append(clientAccountTransaction.finishedAt).append(";")
                    .append(clientAccountTransaction.type).append(";");
            printFile(sb.toString(), TRANSACTIONS);
        }

        private void printClientAccount(ClientAccount clientAccount) {
            if (clientAccount == null) return;
            StringBuilder sb = new StringBuilder();
            sb.append(clientAccount.id).append(";")
                    .append(clientAccount.client).append(";")
                    .append(clientAccount.money).append(";")
                    .append(clientAccount.number).append(";")
                    .append(clientAccount.registeredAt).append(";");
            printFile(sb.toString(), ACCOUNTS);
        }

        private void printClientPhone(ClientPhone clientPhone) {
            if (clientPhone == null) return;
            StringBuilder sb = new StringBuilder();
            sb.append(clientPhone.client).append(";")
            .append(clientPhone.number).append(";")
            .append(clientPhone.type).append(";");
            printFile(sb.toString(), PHONES);
        }

        private void printClientAddress(ClientAddress clientAddress) {
            if (clientAddress == null) return;
            StringBuilder sb = new StringBuilder();
            sb.append(clientAddress.client).append(";")
                    .append(clientAddress.type).append(";")
                    .append(clientAddress.street).append(";")
                    .append(clientAddress.house).append(";")
                    .append(clientAddress.flat).append(";");
            printFile(sb.toString(), ADDRESSES);
        }

        private void printClient(Client client) {
            if (client == null) return;
            StringBuilder sb = new StringBuilder();
            sb.append(client.id).append(";")
            .append(client.surname).append(";")
            .append(client.name).append(";")
            .append(client.patronymic).append(";")
            .append(client.gender).append(";")
            .append(client.birthDate).append(";")
            .append(client.charm).append(";");
            printFile(sb.toString(), CLIENTS);
        }

        private void printFile(String sb, String fileName) {
            try {
                File f = new File(srcDir.getAbsolutePath() + "/" + fileName);
                Files.createDirectories(Paths.get(f.getParent()));
                if (!Files.exists(Paths.get(f.getAbsolutePath())))
                    Files.createFile(Paths.get(f.getAbsolutePath()));
                System.out.println(f.getPath());

                Files.write(Paths.get(f.getAbsolutePath()), (sb + "\n").getBytes(), StandardOpenOption.APPEND);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void removeFile(String file) {
            File f = new File(srcDir.getAbsolutePath() + "/" + file);
            try {
                Files.deleteIfExists(Paths.get(f.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void run() {
        File srcDir = Modules.standDir().toPath().resolve("db/src/kz/greetgo/sandbox/db/stand/beans").toFile();

        new Printer()
                .setClient(
                        new Client(1,
                                "Popov",
                                "Vladimir",
                                "Sergeevich",
                                GenderType.MALE,
                                -127699200,
                                1))
                .setCharm(
                        new Charm(
                                1,
                                "HAPPY",
                                "VERY VERY HAPPY",
                                1))
                .setClientPhone(
                        new ClientPhone(
                                1,
                                "+77777057055",
                                MOBILE
                        ))
                .setClientAddress(
                        new ClientAddress(
                                1,
                                AddressType.REG,
                                "Seyfullina",
                                "15",
                                "33"
                        ))
                .setClientAccount(
                        new ClientAccount(
                                1,
                                1,
                                500,
                                "2562048",
                                new Date().getTime()
                        ))
                .setClientAccountTransaction(
                        new ClientAccountTransaction(
                                1,
                                1,
                                200,
                                new Date().getTime(),
                                1
                        ))
                .setTransactionType(
                        new TransactionType(
                                1,
                                "CODE",
                                "NAME"
                        ))
                .setClient(
                        new Client(
                                2,
                                "Umarov",
                                "Anvar",
                                "Umarovich",
                                GenderType.MALE,
                                new Date().getTime(),
                                2))
                .setCharm(
                        new Charm(
                                2,
                                "SAD",
                                "VERY VERY SAD",
                                (float) 0.5))
                .setClientPhone(
                        new ClientPhone(
                                2,
                                "+77057052255",
                                MOBILE
                        ))
                .setClientAddress(
                        new ClientAddress(
                                1,
                                AddressType.REG,
                                "Tole-bi",
                                "55",
                                "33"
                        ))
                .setClientAccount(
                        new ClientAccount(
                                2,
                                2,
                                10000,
                                "789456",
                                new Date().getTime()
                        ))
                .setClientAccountTransaction(
                        new ClientAccountTransaction(
                                2,
                                2,
                                -100,
                                new Date().getTime(),
                                2
                        ))
                .setTransactionType(
                        new TransactionType(
                                2,
                                "CODE2",
                                "NAME2"
                        ))
                .setClient(
                        new Client(
                                3,
                                "Ualibekova",
                                "Aida",
                                "Ualiyevna",
                                GenderType.FEMALE,
                                913680000,
                                3))
                .setCharm(
                        new Charm(
                                3,
                                "DEPRESSION",
                                "OOH VERY VERY SAD",
                                (float) 0.2))
                .setClientPhone(
                        new ClientPhone(
                                3,
                                "+77777777777",
                                MOBILE
                        ))
                .setClientAddress(
                        new ClientAddress(
                                1,
                                AddressType.REG,
                                "Dostyk",
                                "45b",
                                "78"
                        ))
                .setClientAccount(
                        new ClientAccount(
                                3,
                                3,
                                100000,
                                "78965",
                                new Date().getTime()
                        ))
                .setClientAccountTransaction(
                        new ClientAccountTransaction(
                                3,
                                3,
                                -5000,
                                new Date().getTime(),
                                3
                        ))
                .setTransactionType(
                        new TransactionType(
                                3,
                                "CODE3",
                                "NAME3"
                        ))
                .setSrcDir(srcDir).execute();
    }
}
