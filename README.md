# Gobang 五子棋Discord 機器人
介紹
-------------
這個機器人可以讓你在Discord上跟別人玩五子棋，簡單而有趣的遊戲。遊玩過程中可以進行對話，但機器人將會把對話刪除，不會有把棋局洗到聊天室太上端的問題。

**遊玩示範影片**
https://youtu.be/kiuVlqV4YZE  

**機器人邀請連結**
https://reurl.cc/Zr4vyp

前置設定&指令
-------------  
所有指令皆使用Slash Command(斜線指令)
直接輸入沒有用滴!
![image](https://user-images.githubusercontent.com/70361961/153366048-e17f5618-bf74-4cb2-917b-196a48d71a09.png)  
```/set 類別ID```
邀請機器人進伺服器的第一件事，是設定五子棋戰場的類別。(只有管理員能操作)
設定好之後，棋局頻道就會創建在該類別下方
如下圖
![image](https://user-images.githubusercontent.com/70361961/153370787-81121177-0a2c-4ec0-8b19-dfd63aa67192.png)  
如果要遊玩的的話，只需輸入  
```/play @tag用戶```  
就可以邀請一位用戶跟你對戰啦~  
![image](https://user-images.githubusercontent.com/70361961/153385786-47384cd1-184d-41d6-b3ca-1084edad4434.png)  
剩下的都是透過按鈕來操作  
使用到工具&心得
-------------
**Discord Java API**  
https://github.com/DV8FromTheWorld/JDA  
這個不是第一次接觸的，在之前就做過很多次Bot了。不過其中的Button是我第一次使用，也算是學到新的東西。  
**Graphics2D**    
https://docs.oracle.com/javase/7/docs/api/java/awt/Graphics2D.html  
接下來兩個都是第一次接觸的，這個是Java的繪圖，棋盤與棋子全部都是透過這個來畫出來的    
因為棋盤方方正正的，所以X，Y軸的偏移量就很好抓，落子的定位也就做出來了。  
**Cloudinary**    
接下來這個是一個把圖片上傳到雲端的API，由於Discord的圖片以及檔案一經傳送之後就不能編輯    
無法達到落子時只替換圖片的效果，但是透過編輯圖片URL，就可以實現。    
它的功能非常多，但簡單來說我只是把它上傳上去再獲取圖片URL    
有興趣或有相關需求的也可以去用用看~    
**心得**    
這個作品讓我學習到檔案操控的方式，如:新增、刪除、編輯檔案，以及存放檔案的相對路徑該如何設定  
也學會基本的繪圖，以及二維陣列的實際運用。    
是我目前以來最大的專案，希望大家喜歡><  
