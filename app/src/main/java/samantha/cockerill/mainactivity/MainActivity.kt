package samantha.cockerill.mainactivity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import samantha.cockerill.mainactivity.databinding.RecipeListItemBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    private val recipeListItems = mutableListOf(
        RecipeListItemEntity(1, Unit.Cans, "beans")
    )

    /*private val recipeListItems = MutableLiveData(mutableListOf(
        RecipeListItemEntity(1, Unit.Cans, "beans")
    ))*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            this.applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val recipeListItemsRecyclerView: RecyclerView = findViewById(R.id.recipe_list_items)
                recipeListItemsRecyclerView.adapter = IngredientListAdapter(recipeListItems)
            }
        }
    }

    /*override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.recipe_list_item, parent.parent,false)

        return super.onCreateView(parent, name, context, attrs)
    }*/

    fun addNewRecipeListItem(view: View) {
        val recipeListItemsRecyclerView: RecyclerView = findViewById(R.id.recipe_list_items)
        recipeListItems.add(RecipeListItemEntity(1, Unit.Cans, "beans"))
        recipeListItemsRecyclerView.adapter?.notifyItemInserted((recipeListItems.size ?: 0) - 1)
    }

    fun minusRecipeListItem(view: View) {
        val recipeListItemsRecyclerView: RecyclerView = findViewById(R.id.recipe_list_items)
        recipeListItems.removeLast()
        recipeListItemsRecyclerView.adapter?.notifyItemRemoved((recipeListItems.size ?: 0) - 1)
    }

    fun completeRecipe(view: View) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.recipeDao().insert(
                    RecipeEntity(
                        title = findViewById<EditText>(R.id.recipe_title).text.toString(),
                        recipeListItems = recipeListItems
                    )
                )
            }
        }
    }

}

class IngredientListAdapter(val recipeListItems: MutableList<RecipeListItemEntity>) :
    RecyclerView.Adapter<IngredientListItemViewHolder>(), AdapterView.OnItemSelectedListener {
    //Create ViewHolder, which needs View, so you have to inflate the View from the View ID.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientListItemViewHolder {
        /*  val view = LayoutInflater.from(parent.context)
              .inflate(R.layout.recipe_list_item, parent, false)*/

        val binding = DataBindingUtil.inflate<RecipeListItemBinding>(
            LayoutInflater.from(parent.context), R.layout.recipe_list_item, parent, false
        )

        binding.units.onItemSelectedListener = this

        return IngredientListItemViewHolder(binding, parent.context)
    }

    //Now you have the View, bind the specifics items of said View to the ViewHolder, so it knows what goes where.
    override fun onBindViewHolder(holder: IngredientListItemViewHolder, position: Int) {
        /*holder.quantityInput.setText(recipeListItems[position].quantity.toString())
        holder.unitInput.setSelection(recipeListItems[position].unit.ordinal)
        holder.ingredientInput.setText(recipeListItems[position].ingredient)*/
        holder.bind(recipeListItems[position])
    }

    override fun getItemCount(): Int {
        return recipeListItems.count()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val item = Unit.values().first { it.ordinal == p2 }
        recipeListItems.last().unit = item
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        //Idk
    }

}

class IngredientListItemViewHolder(
    private val binding: RecipeListItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root){

    /*val quantityInput: EditText = binding.findViewById(R.id.quantity)
    val unitInput: Spinner = binding.findViewById(R.id.units)
    val ingredientInput: EditText = binding.findViewById(R.id.ingredient)*/

    init {
        //This was added by meeee in recipe_list_item.xml

        /* this.binding.setVariable(BR.recipeItem, binding)

         val unitInputAdapter = ArrayAdapter(
             context,
             android.R.layout.simple_spinner_item,
             Unit.values().map { it.shorthand })
         unitInput.adapter = unitInputAdapter*/
    }

    fun bind(item: RecipeListItemEntity) {
        val unitInputAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            Unit.values().map { it.shorthand })
        binding.units.adapter = unitInputAdapter

        binding.setVariable(BR.recipeItem, item)
    }

}

data class RecipeListItem(val quantity: Int, val unit: Unit, val ingredient: String)

data class Recipe(val title: String, val dateCreated: Date, val ingredients: List<RecipeListItem>)

enum class Unit(val shorthand: String) {
    Gram("g"),
    Kilogram("kg"),
    Litre("l"),
    Millilitre("ml"),
    Teaspoon("tsp"),
    Tablespoon("tbsp"),
    Cups("cups"),
    Pinch("pinch"),
    Cans("cans")
}