/*
 * Constants.java
 *
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
 *
 */


package Client;

/**
 *
 * @author aqent
 */
public final class Constants {

    public final static byte ORIGIN_ROSTER=0;
    public final static byte ORIGIN_ROSTERRES=1;
    public final static byte ORIGIN_CLONE=2;
    public final static byte ORIGIN_PRESENCE=3;
    public final static byte ORIGIN_GROUPCHAT=4;
//#ifndef WMUC
    public final static byte ORIGIN_GC_MEMBER=5;
    public final static byte ORIGIN_GC_MYSELF=6;
//#endif
    
    
    public final static byte INC_NONE=0;
    public final static byte INC_APPEARING=1;
    public final static byte INC_VIEWING=2;
    
    
    public final static byte AFFILIATION_MEMBER=1;
    public final static byte AFFILIATION_NONE=0;
    public final static byte ROLE_VISITOR=-1;
    public final static byte ROLE_PARTICIPANT=0;
    public final static byte ROLE_MODERATOR=1;    
    public final static byte AFFILIATION_OUTCAST=-1;
    public final static byte AFFILIATION_ADMIN=2;
    public final static byte AFFILIATION_OWNER=3;

    public final static byte GROUP_VISITOR=4;
    public final static byte GROUP_MEMBER=3;
    public final static byte GROUP_PARTICIPANT=2;
    public final static byte GROUP_MODERATOR=1;

}
