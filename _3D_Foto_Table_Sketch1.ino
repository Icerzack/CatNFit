 
#define  Pin0 8  //пины входы драйвера ULN2003A
#define  Pin1 10 //пины входы драйвера ULN2003A
#define  Pin2 11 //пины входы драйвера ULN2003A
#define  Pin3 12 //пины входы драйвера ULN2003A
#define  Pin9 9  //пин управления ИК светодиода

#include <multiCameraIrControl.h> //библиотека для управления фотокамерами

  int _step = 0; 
  int count = 0;
  boolean dir = true;// направление вращения стола true - по часовой, false - против
  int A = 0;// градус начального положения стола. Меняется от 0 до 360 градусов.
  int B; // новое положения стола.
  int C; // коррекция в градусах которою нужно алгебраически прибавить A чтобы получить B.
  int Serial_bluetooth_int = -1;              //цифра - сигнал управления, принемаемый по bluetooth
  int count_photo = 0; // количество фото на оборот
  int stay_photo; // счетчик оставшихся фото
  float deegres_to_frame;// градусы на каждый кадр при повороте на 360 градусов
 
  Canon canon(Pin9); //создаем  объекта для пульта ИК
  Nikon nikon(Pin9); //создаем  объекта для пульта ИК
  boolean case_of_photo = true ; // тип фотоаппарата: true - Nikon, false - Canon
  
  void setup(){ 
      Serial1.begin(9600);        //стартуем блютуз, скорость общения по Bluetooth 9600, для Ардуино UNO будет Serial.begin(9600); и далее везде где Serial1 pзаменить на Serial
      Serial1.setTimeout(5);      //устанавливаем время ожидания всей посылки от пульта (максимум 3 цифры),  для Ардуино UNO будет Serial.setTimeout(5); 
      pinMode(Pin0, OUTPUT);  
      pinMode(Pin1, OUTPUT);  
      pinMode(Pin2, OUTPUT);  
      pinMode(Pin3, OUTPUT);  
 } 
  void loop() {
     if(Serial1.available()>0){
       Serial_bluetooth_int = Serial1.parseInt(); //считываем целочисленное число из последовательного порта
     }
     if (0<=Serial_bluetooth_int && Serial_bluetooth_int<=360){
       B = Serial_bluetooth_int;  
      //-------------------------блок управления поворотом стола без фото -------------------------------
      // A - существующее положение стола; В - требуемое положение; С - нужная коррекция в градусах, для занятия 
      //требуемого положения из начального кратчайшим путём; dir = true - вращаем по часовой стрелке, false - против часовой 
       if(0 <= A && A <= 180){ //если А в 1 и во 2 квадранте
          if(A <= B && B <= A+180){ // если В между А и А+180
            dir = false;
            C = B - A; // А крутится против часовой на место В 
          }
           else if(A+180 < B && B <= 360){ // если В между А+180 и 360( 0 )
             dir = true;
             C = A+(360-B); //А крутится по часовой на место В
           }
           else if(0 <= B && B <= A){ // если В между 0 и А
             dir = true;
             C = A - B; // А крутится по часовой на место В
           } 
        }
        else if(180 < A && A <= 270){ //если А в 3 квадранте
          if(A < B && B <= 360){ // если В между А и 360
            dir = false; 
            C = B - A; // А крутится против часовой на место В 
          }
           else if(A-180 < B && B <= A){ // если В между А-180 и A
            dir = true;
            C = A - B; //А крутится по часовой на место В
           }
           else if(0 <= B && B <= A-180){ // если В между 0 и А-180
             dir = false; 
             C = B+(360-A); // А крутится по часовой на место В
           }
         }
        else  if(270 < A && A <= 360){ //если А в 4 квадранте
          if(A < B && B <= 360){ // если В между А и 360
            dir = false; 
            C = B - A; // А крутится против часовой на место В 
          }
           else if(A-180 < B && B <= A){ // если В между А-180 и A
            dir = true;
            C = A - B; //А крутится по часовой на место В
           }
           else if(0 <= B && B <= A-180){ // если В между 0 и А-180
             dir = false; 
             C = B+(360-A); // А крутится по часовой на место В
           }
         }
     //-------------------------конец блока управления поворотом стола без фото -------------------------------  
       A = B; // теперь это новое начальное положение
       C= map(C,0,360,0,4096);//преобразуем значение С из диапазона 360 градусов стола в 4096 шагов двигателя
       Step8(dir, C);//перемещаем стол
       Serial_bluetooth_int=-1;
    } // end  if (<Serial_bluetooth_int && Serial_bluetooth_int<=360)
    
    if (400<Serial_bluetooth_int && Serial_bluetooth_int<=760){ // принимаем код количества зададанных фото на оборот, реально их на 400 меньше  
      count_photo = Serial_bluetooth_int - 400;
      deegres_to_frame = 360/count_photo;
      deegres_to_frame = map(deegres_to_frame,0,360,0,4096);
      for(int i = 1; i <= count_photo; i++){
        SutterNow();//фотографируем
        stay_photo = count_photo-i;
        Serial1.print(stay_photo);
        delay(1500); //задержка после фото
        Step8(true, deegres_to_frame); //крутим мотор по часовой стрелке на угол deegres_to_frame
        delay(2500);//задержка после поворота
     
        if(Serial1.available()>0){
          Serial_bluetooth_int = Serial1.parseInt(); //считываем целочисленное число для контроля паузы или сброса
        }
        switch(Serial_bluetooth_int){
          case 999://пауза
            Serial_bluetooth_int=-1;
            while (Serial_bluetooth_int!=999){
              if(Serial1.available()>0){
              Serial_bluetooth_int = Serial1.parseInt(); //считываем целочисленное число для контроля паузы    
              }
            }
          break;
         
          case 990://сброс
          (deegres_to_frame*i) < count_photo/2 ? Step8(true, (deegres_to_frame*(count_photo - i))) : Step8(false, (deegres_to_frame*i)) ; //выкручиваем стол в начальную позицию
          Serial_bluetooth_int=-1;
          Serial1.print ("0"); //обнуляем счётчик на пульте
          goto reset_label;
          break;
       }
       
     }//end for
     Serial_bluetooth_int = -1;
   }
    reset_label:
    
    switch(Serial_bluetooth_int){
     case 931:
      case_of_photo = false; // тип фотоаппарата Canon
      Serial_bluetooth_int = -1;
      break;
     
     case 930:
      case_of_photo = true; // тип фотоаппарата Nikon
      Serial_bluetooth_int = -1;
      break;
      
    case 995:
      SutterNow ();
      Serial_bluetooth_int = -1;
    break;
    }//end switch
    
  }//end loop
   
  void SutterNow (void){ //фотографирование в зависимости от камеры
   case_of_photo ? nikon.shutterNow(): canon.shutterNow();//выбираеи никон или кэнон
  }
       
  void Step8(boolean direct, int c){// "с"  шагов мотора в направлении direct по 8 последовательных шагов
    count=0;
    while(count <= c){       
    switch(_step){ 
      case 0: 
        digitalWrite(Pin0, 0);  
        digitalWrite(Pin1, 0); 
        digitalWrite(Pin2, 0); 
        digitalWrite(Pin3, 1); 
      break;  
      case 1: 
        digitalWrite(Pin0, 0);  
        digitalWrite(Pin1, 0); 
        digitalWrite(Pin2, 1); 
        digitalWrite(Pin3, 1); 
      break;  
      case 2: 
        digitalWrite(Pin0, 0);  
        digitalWrite(Pin1, 0); 
        digitalWrite(Pin2, 1); 
        digitalWrite(Pin3, 0); 
      break;  
      case 3: 
        digitalWrite(Pin0, 0);  
        digitalWrite(Pin1, 1); 
        digitalWrite(Pin2, 1); 
        digitalWrite(Pin3, 0); 
      break;  
      case 4: 
        digitalWrite(Pin0, 0);  
        digitalWrite(Pin1, 1); 
        digitalWrite(Pin2, 0); 
        digitalWrite(Pin3, 0); 
      break;  
      case 5: 
        digitalWrite(Pin0, 1);  
        digitalWrite(Pin1, 1); 
        digitalWrite(Pin2, 0); 
        digitalWrite(Pin3, 0); 
      break;  
        case 6: 
        digitalWrite(Pin0, 1);  
        digitalWrite(Pin1, 0); 
        digitalWrite(Pin2, 0); 
        digitalWrite(Pin3, 0); 
      break;  
      case 7: 
        digitalWrite(Pin0, 1);  
        digitalWrite(Pin1, 0); 
        digitalWrite(Pin2, 0); 
        digitalWrite(Pin3, 1); 
      break;  
      default: 
        digitalWrite(Pin0, 0);  
        digitalWrite(Pin1, 0); 
        digitalWrite(Pin2, 0); 
        digitalWrite(Pin3, 0); 
      break;  
    }//end switch(_step)
        if(direct){ 
          _step++; 
          count++;
        }
        else{ 
          _step--; 
          count++;
        } 
        if(_step>7){ 
          _step=0; 
        } 
        if(_step<0){ 
          _step=7; 
        }
       delay(1);//пауза между шагами, даёт возможность воздействовать на обмотки мотора
    }
  }

