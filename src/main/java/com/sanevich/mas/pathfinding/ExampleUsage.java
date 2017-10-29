/*    
    Copyright (C) 2012 http://software-talk.org/ (developer@software-talk.org)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.sanevich.mas.pathfinding;

import java.util.List;

/**
 * A simple example for the usage of this package.
 * 
 * @see ExampleFactory
 * @see Point
 */
public class ExampleUsage {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AstarMap<Point> myAstarMap = new AstarMap<>(50, 50, new ExampleFactory());
        List<Point> path = myAstarMap.findPath(0, 0, 40, 40);

        for (int i = 0; i < path.size(); i++) {
            System.out.print("(" + path.get(i).getxPosition() + ", " + path.get(i).getyPosition() + ") -> ");
        }
    }


}
