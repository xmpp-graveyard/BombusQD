/*
 * AccountForm.java
 *
 * Created on 20.05.2008, 13:05
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package Account;

import Client.*;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.VirtualList;
import ui.controls.AlertBox;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.NumberInput;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;
import util.StringLoader;
import ui.controls.form.MultiLine;
import util.StringUtils;
import java.util.Random;
import ui.controls.form.SpacerItem;
import ui.controls.form.SimpleString;

public class AccountForm 
        extends DefForm {

    private final AccountSelect accountSelect;

    private TextInput userbox;
    private TextInput passbox;
    private TextInput servbox;
    private TextInput ipbox;
    private NumberInput portbox;
    private TextInput resourcebox;
    private TextInput nickbox;
    private CheckBox sslbox;
    private CheckBox plainPwdbox;
    private CheckBox compressionBox;
    private CheckBox confOnlybox;
    private TextInput emailbox;    
//#if HTTPCONNECT
//#       private CheckBox proxybox;
//#elif HTTPPOLL        
//#       private CheckBox pollingbox;
//#endif
    private CheckBox registerbox;
    
    private NumberInput keepAlive;
    private DropChoiceBox keepAliveType;
    
//#if HTTPPOLL || HTTPCONNECT  
//#     private TextInput proxyHost;
//#     private TextInput proxyPort;
//#endif

    Account account;
    
    boolean newaccount;
    
    boolean showExtended;
    
    LinkString link_genPass; 
    LinkString link_genServer;   
    LinkString insertpass;

    private int type_profile = -1; 
    boolean register=false;
    String serverReg="";
    
    private static final  int JABBER_PROFILE=1;    
    private static final  int YARU_PROFILE=2;    
    private static final  int GTALK_SSL_PROFILE=3;    
    private static final  int LJ_PROFILE=4;    
    private static final  int QIP_PROFILE=5;
    private static final  int GTALK_HTTPS_PROFILE=6;        
    
    /** Creates a new instance of AccountForm */

    public AccountForm(Display display, Displayable pView, AccountSelect accountSelect, Account account,int type_profile,
            boolean register,String serverReg) {
       super(display, pView, null);
       this.type_profile=type_profile;
       this.register=register;
       this.serverReg=serverReg;
       
	this.accountSelect = accountSelect;
        this.display=display;
        
	newaccount=(account==null);
	if (newaccount) account=new Account();
	this.account=account;
	
	String mainbar = (newaccount)?SR.get(SR.MS_NEW_ACCOUNT):(account.toString());
        if(register){
          getMainBarItem().setElementAt(SR.get(SR.MS_REGISTER), 0);
        }else{
          getMainBarItem().setElementAt(mainbar, 0);
        }

        userbox = new TextInput(display, SR.get(SR.MS_USERNAME), account.getUserName(), null, TextField.ANY);
        itemsList.addElement(userbox);//1
        String server=register?serverReg:"";
        String password=account.getPassword();
        int port_box=5222;
        if(!register){
          switch(type_profile){
            case -1:   
                server=account.getServer();
                port_box = account.getPort();
                break;            
            case JABBER_PROFILE:  
                server=generate(0);
                break;
            case YARU_PROFILE:   
                server="ya.ru";
                break;
            case GTALK_HTTPS_PROFILE:
            case GTALK_SSL_PROFILE:   
                server="gmail.com";
                port_box = 5223;
                break;
            case LJ_PROFILE:     
                server="livejournal.com";
                break;
            case QIP_PROFILE:     
                server="qip.ru";
                break;
          }
        }else{
           password=generate(1);
        }
        servbox = new TextInput(display, SR.get(SR.MS_SERVER), server, null, TextField.ANY);
        itemsList.addElement(servbox);   
        server=null;
        
        if(!register){
          link_genServer = new LinkString(SR.get(SR.MS_GENERATE)+" "+SR.get(SR.MS_SERVER)) { public void doAction() { 
           servbox.setValue(generate(0));
          } };
          itemsList.addElement(link_genServer);
          itemsList.addElement(new SpacerItem(5));
        }
        
        passbox = new TextInput(display, SR.get(SR.MS_PASSWORD), password,null,TextField.ANY);     
        itemsList.addElement(passbox);
        
        link_genPass = new LinkString(SR.get(SR.MS_GENERATE)+" "+SR.get(SR.MS_PASSWORD)) { public void doAction() { 
         passbox.setValue(generate(1));
        } };
        itemsList.addElement(link_genPass); 
        itemsList.addElement(new SpacerItem(5));
        
        resourcebox = new TextInput(display, SR.get(SR.MS_RESOURCE), account.getResource(), null, TextField.ANY);
        itemsList.addElement(resourcebox);
        
        portbox = new NumberInput(display, SR.get(SR.MS_PORT), Integer.toString(port_box), 0, 65535);
        itemsList.addElement(portbox);
        
    	emailbox = new TextInput(display,"E-mail:",account.getEmail(),null, TextField.EMAILADDR);
        if(midlet.BombusQD.cf.userAppLevel>=1) {
          itemsList.addElement(emailbox);
        }

        if(midlet.BombusQD.clipboard.getClipBoard()!=null){
            if(midlet.BombusQD.clipboard.getClipBoard().startsWith("!")){
              insertpass = new LinkString(SR.get(SR.MS_INSERT_NEW_PASSWORD)) { public void doAction() { 
                passbox.setValue(midlet.BombusQD.clipboard.getClipBoard().substring(1));
                itemsList.removeElement(insertpass);
                midlet.BombusQD.clipboard.setClipBoard("");
              } };
              itemsList.addElement(new SpacerItem(3));              
              itemsList.addElement(insertpass);
            }
        }

        nickbox = new TextInput(display, SR.get(SR.MS_NICKNAME), account.getNick(), null, TextField.ANY);
        itemsList.addElement(nickbox);
        
        registerbox = new CheckBox(SR.get(SR.MS_REGISTER_ACCOUNT), register); 
        
        if (newaccount){
            itemsList.addElement(registerbox);            
        }

        if(!register){
          showExtended(); 
        }

        attachDisplay(display);
        this.parentView=pView;
    }
    

   public String generate(int type) {
    StringBuffer sb = new StringBuffer(0);      
    if(type==0){   
      Random rand = new Random();
      int i=0;
      String[] servers = {
          "jabber.ru","jabbim.com","jabbus.org","xmpp.ru","jtalk.ru",
          "mytlt.ru","gajim.org","jabber.org.by"};
      i = Math.abs(rand.nextInt()) % 8;
      sb.append(servers[i]);
    }
    else if(type==1){
      Random rand = new Random();
      int i=0;
      char[] chars = {
          'q','w','e','r','t','y','u','i','o','p',
          'a','s','d','f','g','h','j','k','l','z',
          'x','c','v','b','n','m','Q','W','E','R',
          'T','Y','U','I','O','P','A','S','D','F',
          'G','H','J','K','L','Z','X','C','V','B',
          'N','M','0','1','2','3','4','5','6','7',
          '8','9'}; //62
      char[] pass = {'*','*','*','*','*','*','*','*'};
      for (int k = 0; k<pass.length; k++) {
         i = Math.abs(rand.nextInt()) % 62;
         pass[k]=chars[i];
      }
      sb.append(pass);        
    }
    return sb.toString();
  }     
    

  public void showExtended() {
        showExtended=true;
        if (!newaccount){
            itemsList.addElement(registerbox);   
        }
        boolean portbox_=false;
        boolean sslbox_=false;
        boolean plainPwdbox_=false;
        boolean compressionBox_=false;  
        String ip_box="";
        switch(type_profile){
            case -1:  
                ip_box = account.getHostAddr();
                sslbox_ = account.getUseSSL();
                plainPwdbox_ = account.getPlainAuth();
                compressionBox_ = account.useCompression();
                break;
            case JABBER_PROFILE://
                ip_box ="";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = !register;//true;
                break;
            case YARU_PROFILE:
                ip_box = "xmpp.yandex.ru";
                sslbox_ = true;
                plainPwdbox_ = true;
                compressionBox_ = false;
                break;
            case GTALK_HTTPS_PROFILE:
                ip_box = "talk.google.com";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = false;
                break;
            case GTALK_SSL_PROFILE:  
                ip_box = "talk.google.com";
                sslbox_ = true;
                plainPwdbox_ = true;
                compressionBox_ = false;
                break;
            case LJ_PROFILE:     
                ip_box = "xmpp.services.livejournal.com";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = false;
                break;
            case QIP_PROFILE:  
                ip_box = "webim.qip.ru";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = true;                
                break;
        }
        ipbox = new TextInput(display, SR.get(SR.MS_HOST_IP), ip_box, null, TextField.ANY);
        sslbox = new CheckBox(SR.get(SR.MS_SSL), sslbox_);
        plainPwdbox = new CheckBox(SR.get(SR.MS_PLAIN_PWD), plainPwdbox_);
        compressionBox = new CheckBox(SR.get(SR.MS_COMPRESSION), compressionBox_);
        confOnlybox = new CheckBox(SR.get(SR.MS_CONFERENCES_ONLY), account.isMucOnly());
//#if HTTPCONNECT
//#        proxybox = new CheckBox("proxybox", SR.get(SR.MS_PROXY_ENABLE), account.isEnableProxy());
//#elif HTTPPOLL        
//#        pollingbox = new CheckBox("pollingbox", "HTTP Polling", false);
//#endif

        itemsList.addElement(sslbox);
        itemsList.addElement(plainPwdbox);
        itemsList.addElement(compressionBox);
        itemsList.addElement(confOnlybox);
//#if HTTPCONNECT
//#        itemsList.addElement(proxybox);
//#elif HTTPPOLL        
//#        itemsList.addElement(pollingbox);
//#endif

        keepAliveType=new DropChoiceBox(display, SR.get(SR.MS_KEEPALIVE));
        keepAliveType.append("by socket");
        keepAliveType.append("1 byte");
        keepAliveType.append("<iq/>");
        keepAliveType.append("ping");
        keepAliveType.setSelectedIndex(account.getKeepAliveType());
        keepAlive = new NumberInput(display, SR.get(SR.MS_KEEPALIVE_PERIOD), Integer.toString(account.getKeepAlivePeriod()), 10, 2048);
        //if(Config.getInstance().difficulty_level>=1){
             itemsList.addElement(keepAliveType);
             itemsList.addElement(keepAlive);
        //} 
             
//#if HTTPCONNECT
//# 	proxyHost = new TextInput(display, SR.get(SR.MS_PROXY_HOST), account.getProxyHostAddr(), null, TextField.URL);
//# 
//# 	proxyPort = new TextInput(display, SR.get(SR.MS_PROXY_PORT), Integer.toString(account.getProxyPort()));
//#elif HTTPPOLL        
//# 	proxyHost = new TextInput(display, SR.get(SR.MS_PROXY_HOST), account.getProxyHostAddr(), null, TextField.URL);
//#endif
        //if(Config.getInstance().difficulty_level>=1){
          itemsList.addElement(ipbox);
        //}

        //if(Config.getInstance().difficulty_level>=1){
        //}

//#if HTTPCONNECT
//# 	itemsList.addElement(proxyHost);
//# 	itemsList.addElement(proxyPort);
//#elif HTTPPOLL        
//# 	itemsList.addElement(proxyHost);
//#endif
    }
    
    public void cmdOk() {
        midlet.BombusQD.debug.add("::saved",10);
        String user = userbox.getValue().trim().toLowerCase();
        String server = servbox.getValue().trim().toLowerCase();
        String pass = passbox.getValue();
        int at = user.indexOf('@');
        if (at>-1) {
            server=user.substring(at+1);
            user=user.substring(0, at);
        }
        if (server.length()==0 || user.length()==0 || pass.length()==0)
            return;
        
        account.setUserName(user);
        account.setServer(server);
        account.setPort(Integer.parseInt(portbox.getValue()));
        account.setEmail(emailbox.getValue().trim());        
        account.setPassword(pass);
        account.setNick(nickbox.getValue());
        account.setResource(resourcebox.getValue());
       
        
        boolean registerNew = false;
        
        if (newaccount){
            registerNew=registerbox.getValue();
        }

        if (showExtended) {
            registerNew=registerbox.getValue();
            account.setHostAddr(ipbox.getValue());
            account.setUseSSL(sslbox.getValue());
            account.setPlainAuth(plainPwdbox.getValue());
            account.setUseCompression(compressionBox.getValue());
            account.setMucOnly(confOnlybox.getValue());

//#if HTTPPOLL || HTTPCONNECT            
//#         account.setEnableProxy(proxybox.getValue());
//#         account.setProxyHostAddr(proxyHost.getValue());
//#         account.setProxyPort(proxyPort.getValue());
//#endif

            account.setKeepAlivePeriod(Integer.parseInt(keepAlive.getValue()));
            account.setKeepAliveType(keepAliveType.getValue());
        }

        if (newaccount) 
            accountSelect.accountList.addElement(account);
        accountSelect.rmsUpdate();
        accountSelect.commandState();

        if (registerNew) {
            new AccountRegister(account, display, parentView); 
        } else {
            destroyView();
        }
        account=null;
    }

    public void destroyView(){
        display.setCurrent(accountSelect);
    }
    
    private void startLogin(boolean login){
        Config.getInstance().accountIndex=accountSelect.accountList.size()-1;
        Account.loadAccount(login, Config.getInstance().accountIndex,-1);
        midlet.BombusQD.getInstance().s.close();
    }
    
    
//#ifdef MENU_LISTENER    
    public void userKeyPressed(int keyCode){
     switch (keyCode) {
        case KEY_NUM4:
            pageLeft();
            break; 
        case KEY_NUM6:
            pageRight();
            break;  
     }
    }
//#endif     
    
    
    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold==keyCode) return;
        kHold=keyCode;
        
        if (keyCode==KEY_NUM6) {
            Config cf=Config.getInstance();
            cf.fullscreen=!cf.fullscreen;
            cf.saveToStorage();
            VirtualList.fullscreen=cf.fullscreen;
            StaticData.getInstance().roster.setFullScreenMode(cf.fullscreen);
        }
    }
}
