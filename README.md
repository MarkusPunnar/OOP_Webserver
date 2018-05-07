# OOP_Webserver

###IntelliJ õpetus serveri kasutamiseks

1) Ava projekt IntelliJ-s.
2) Määra WebServeri classi (/server/src/main/java/webserver) configurationis Program Argumentiks kaust, kus soovid, et server jookseks.
3) File - Project Structure - Modules. Serveri moodulile lisada dependencyiteks kõik pluginad. Scope peab olema runtime.
4) Käivita klassi WebServeri main meetod. 
5) PROFIT $  € EEK

###Käsurea õpetus serveri kasutamiseks

1) Ava käsureal projekti juurkaust. 
2) Jooksuta käsklus "mvn clean package", et luua pluginate jar failid.
3) Leia pluginate target kaustadest jar failid.
4) Jooksuta käsklus "java -cp \<Serveri jari full path\>;\<Plugina jari full path\> webserver.WebServer <Kaust, kus server jookseb>"
5) Pluginate jare võib eelmises käskluses olla kuitahes palju. Eraldajaks nende vahel on semikoolon vaid Windowsi puhul, muude operatsioonisüsteemidega on selleks koolon.
 
