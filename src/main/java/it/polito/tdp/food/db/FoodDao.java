package it.polito.tdp.food.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.food.model.Condiment;
import it.polito.tdp.food.model.Edge;
import it.polito.tdp.food.model.Food;
import it.polito.tdp.food.model.Portion;

public class FoodDao {
	public void listAllFoods(Map<Integer, Food> idMap){
		String sql = "SELECT * FROM food" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
						
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					Food food=new Food(res.getInt("food_code"),
							res.getString("display_name")
							);
					
					idMap.put(food.getFood_code(), food);
					
				} catch (Throwable t) {
					t.printStackTrace();
				}
				
			}
			
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public List<Condiment> listAllCondiments(){
		String sql = "SELECT * FROM condiment" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Condiment> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Condiment(res.getInt("condiment_code"),
							res.getString("display_name"),
							res.getDouble("condiment_calories"), 
							res.getDouble("condiment_saturated_fats")
							));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Portion> listAllPortions(){
		String sql = "SELECT * FROM portion" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Portion> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Portion(res.getInt("portion_id"),
							res.getDouble("portion_amount"),
							res.getString("portion_display_name"), 
							res.getDouble("calories"),
							res.getDouble("saturated_fats"),
							res.getInt("food_code")
							));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}

	}
	
	public List<Food> getVertexes(int min, Map<Integer, Food> idMap){
		String sql = "SELECT p.food_code as fc "
				+ "FROM `portion` p "
				+ "GROUP BY p.food_code "
				+ "HAVING COUNT(*) > ?";
		
		List<Food> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, min);
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					Food food=idMap.get(res.getInt("fc"));
					
					result.add(food);
					
				} catch (Throwable t) {
					t.printStackTrace();
				}
				
			}
			
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	// In effetti qua non mi servivano tutte le cose di portion
	
	/*public List<Edge> getEdges(Map<Integer, Food> idMap){
		String sql = "SELECT p1.food_code as f1, p2.food_code as f2, AVG(c.condiment_calories) as cond "
				+ "FROM `portion` p1, `portion` p2, food_condiment fc1, food_condiment fc2, condiment c "
				+ "WHERE fc1.food_code=p1.food_code AND fc2.food_code=p2.food_code "
				+ " AND fc1.condiment_code=fc2.condiment_code "
				+ " AND fc1.condiment_code=c.condiment_code "
				+ "GROUP BY p1.food_code, p2.food_code "
				+ "HAVING COUNT(*)>1";
		
		
		List<Edge> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					Edge edge = new Edge(idMap.get(res.getInt("f1")),idMap.get(res.getInt("f2")),res.getDouble("cond"));
					
					result.add(edge);
					
				} catch (Throwable t) {
					t.printStackTrace();
				}
				
			}
			
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		

	}*/
	
	public Double calorieCongiunte(Food f1, Food f2) {
		String sql = "SELECT fc1.food_code, fc2.food_code,  " + 
				"		 AVG(condiment.condiment_calories) AS cal " + 
				"FROM food_condiment AS fc1, food_condiment AS fc2, condiment " + 
				"WHERE fc1.condiment_code=fc2.condiment_code " + 
				"AND condiment.condiment_code=fc1.condiment_code " + 
				"AND fc1.id<>fc2.id " + 
				"AND fc1.food_code=? " + 
				"AND fc2.food_code=? " + 
				"GROUP BY fc1.food_code, fc2.food_code" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, f1.getFood_code());
			st.setInt(2, f2.getFood_code());
			
			ResultSet res = st.executeQuery() ;
			
			Double calories = null ;
			if(res.first()) {
				calories = res.getDouble("cal") ;
			}
			// altimenti rimane null
			
			conn.close();
			return calories ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	
	}

	
}
