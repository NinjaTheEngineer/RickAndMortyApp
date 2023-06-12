package com.ninja.rickandmortyapp.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ninja.rickandmortyapp.repository.CharacterRepository
import com.ninja.rickandmortyapp.utils.ImageLoader
import com.ninja.rickandmortyapp.R
import com.ninja.rickandmortyapp.adapter.CharacterAdapter
import com.ninja.rickandmortyapp.model.Character
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharacterListActivity : AppCompatActivity() {
    private val TAG = "CharacterListActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var characterDetailsLayout: LinearLayout
    private lateinit var portalCardView: CardView
    private lateinit var initialTextView: TextView
    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var characterRepository: CharacterRepository
    private lateinit var clearSelectionButton: Button
    private lateinit var searchEditText: EditText
    private val imageLoader = ImageLoader()

    private var originalCharacterList: List<Character> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_list)

        recyclerView = findViewById(R.id.recyclerView)
        characterDetailsLayout = findViewById(R.id.characterDetailsLayout)
        portalCardView = findViewById(R.id.portalCardView)
        initialTextView = findViewById(R.id.initialTextView)
        clearSelectionButton = findViewById(R.id.clearSelectionButton)
        searchEditText = findViewById(R.id.searchEditText)

        setPortalRotation()
        setOrderButtons()

        characterRepository = CharacterRepository()
        characterAdapter = CharacterAdapter(::onCharacterSelected)
        recyclerView.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = characterAdapter

        characterDetailsLayout.visibility = LinearLayout.GONE

        clearSelectionButton.setOnClickListener {
            clearSelection()
        }
        if(characterRepository.isNetworkAvailable(this)) {
            fetchCharacters()
        } else {
            showNoInternetDialog()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCharacters(s?.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun setOrderButtons() {
        val orderByGenderButton: Button = findViewById(R.id.orderByGenderButton)
        val orderByNameButton: Button = findViewById(R.id.orderByNameButton)
        val orderByEpisodesButton: Button = findViewById(R.id.orderByEpisodesButton)

        orderByGenderButton.setOnClickListener {
            characterAdapter.orderBy(CharacterAdapter.SortType.GENDER)
        }
        orderByNameButton.setOnClickListener {
            characterAdapter.orderBy(CharacterAdapter.SortType.NAME)
        }
        orderByEpisodesButton.setOnClickListener {
            characterAdapter.orderBy(CharacterAdapter.SortType.EPISODES)
        }
    }
    private fun setPortalRotation() {
        val rotationAnimator = ObjectAnimator.ofFloat(portalCardView, "rotation", 0f, 360f).apply {
            duration = 10000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        rotationAnimator.start()
    }
    private fun clearSelection() {
        characterDetailsLayout.visibility = LinearLayout.GONE
        portalCardView.visibility = CardView.VISIBLE
        initialTextView.visibility = TextView.VISIBLE
        searchEditText.text.clear()
    }
    private fun fetchCharacters() {
        CoroutineScope(Dispatchers.Main).launch {
            val initialCharacters = withContext(Dispatchers.IO) {
                characterRepository.getInitialCharacters()
            }
            characterAdapter.submitList(initialCharacters)

            CoroutineScope(Dispatchers.IO).launch {
                val allCharacters = characterRepository.getAllCharacters()
                originalCharacterList = allCharacters
                withContext(Dispatchers.Main) {
                    characterAdapter.submitList(allCharacters)
                }
            }
        }
    }
    private fun filterCharacters(query: String?) {
        if (query.isNullOrBlank()) {
            characterAdapter.submitList(originalCharacterList)
        } else {
            val filteredList = originalCharacterList.filter { character ->
                character.name.contains(query, ignoreCase = true)
            }
            characterAdapter.submitList(filteredList)
        }
    }
    private fun onCharacterSelected(character: Character) {
        characterDetailsLayout.visibility = LinearLayout.VISIBLE
        portalCardView.visibility = CardView.GONE
        initialTextView.visibility = TextView.GONE
        clearSelectionButton.visibility = LinearLayout.VISIBLE
        val rootView = findViewById<View>(android.R.id.content)
        rootView.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
        imageLoader.loadIntoImageView(this, character.image, findViewById(R.id.characterImage))
        findViewById<TextView>(R.id.characterNameTextView).text = character.name
        findViewById<TextView>(R.id.speciesValueTextView).text = character.species
        findViewById<TextView>(R.id.genderValueTextView).text = character.gender
        findViewById<TextView>(R.id.statusValueTextView).text = character.status
        findViewById<TextView>(R.id.originValueTextView).text = character.origin.name
        findViewById<TextView>(R.id.locationValueTextView).text = character.location.name
        findViewById<TextView>(R.id.numOfEpsValueTextView).text = character.episode.size.toString()
    }
    private fun showNoInternetDialog() {
        val dialogBuilder = AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        dialogBuilder.setTitle(R.string.no_internet_access)
            .setMessage(R.string.check_internet_and_retry_later)
            .setPositiveButton(R.string.close_app, DialogInterface.OnClickListener { _, _ ->
                finishAffinity()
            })
            .setCancelable(false)
            .show()
    }
}