package it.polito.tdp.nyc.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.nyc.model.Hotspot;
import it.polito.tdp.nyc.model.NTA;

public class NYCDao {
	
	public List<Hotspot> getAllHotspot(){
		String sql = "SELECT * FROM nyc_wifi_hotspot_locations";
		List<Hotspot> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Hotspot(res.getInt("OBJECTID"), res.getString("Borough"),
						res.getString("Type"), res.getString("Provider"), res.getString("Name"),
						res.getString("Location"),res.getDouble("Latitude"),res.getDouble("Longitude"),
						res.getString("Location_T"),res.getString("City"),res.getString("SSID"),
						res.getString("SourceID"),res.getInt("BoroCode"),res.getString("BoroName"),
						res.getString("NTACode"), res.getString("NTAName"), res.getInt("Postcode")));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}
	
	/**
	 * Primo punto: prendiamo tutti i borghi
	 * ordinati in ordine alfabetico
	 * @return
	 */
	public List<String> getAllBorough(){
		String sql = "SELECT DISTINCT Borough "
				+ "FROM nyc_wifi_hotspot_locations "
				+ "ORDER BY Borough";
		List<String> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				String borgo = res.getString("borough");
				result.add(borgo);
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}
	
	
	public List<NTA> getAllNTAfromBorough(String borough){
		String sql = "SELECT distinct NTACode, SSID "
				+ "FROM nyc_wifi_hotspot_locations "
				+ "WHERE borough = ? "
				+ "ORDER BY NTACode";
		List<NTA> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, borough);
			ResultSet res = st.executeQuery();

			String lastNTA = "";
			while (res.next()) {
				if(!res.getString("NTACode").equals(lastNTA)) {
					Set<String> setssid = new HashSet<>();
					setssid.add(res.getString("SSID"));
					result.add(new NTA(res.getString("NTACode"),setssid));
					lastNTA = res.getString("NTACode");
				}
				else {
					result.get(result.size()-1).getSSID().add(res.getString("SSID"));
				}
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}
	
}
