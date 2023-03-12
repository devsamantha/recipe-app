package samantha.cockerill.mainactivity

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

@Database(entities = [RecipeListItemEntity::class, RecipeEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}

@Entity(tableName = "recipeListItem")
data class RecipeListItemEntity(
    @ColumnInfo var quantity: Int,
    @ColumnInfo var unit: Unit,
    @PrimaryKey var ingredient: String
)

@Entity(tableName = "recipe")
data class RecipeEntity(
    @PrimaryKey var title: String,
    @ColumnInfo var recipeListItems: List<RecipeListItemEntity>
)

@Dao
interface RecipeDao {
    @Insert
    fun insert(recipe: RecipeEntity)

    @Query("SELECT * FROM recipe WHERE title = (:title)")
    fun get(title: String): RecipeEntity
}

class Converters {
    @TypeConverter
    fun fromJsonToRecipeItems(value: String): List<RecipeListItemEntity> {
        val listType: Type = object : TypeToken<List<RecipeListItemEntity>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromRecipeItemsToJson(list: List<RecipeListItemEntity>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}