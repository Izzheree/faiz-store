package com.faiz0033.faizstore.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faiz0033.faizstore.R
import com.faiz0033.faizstore.domain.repository.LaptopRepository
import com.faiz0033.faizstore.presentation.common.components.EmptyState
import com.faiz0033.faizstore.presentation.common.components.ErrorState
import com.faiz0033.faizstore.presentation.common.components.LoadingState
import com.faiz0033.faizstore.presentation.home.components.LaptopCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    laptopRepository: LaptopRepository,
    ownerEmail: String,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(laptopRepository, ownerEmail)
    )
    
    LaunchedEffect(Unit) {
        viewModel.sync()
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.profile))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_laptop))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_laptops)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> LoadingState()
                    is HomeUiState.Empty -> EmptyState()
                    is HomeUiState.Error -> ErrorState(message = state.message)
                    is HomeUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = state.laptops,
                                key = { it.id }
                            ) { laptop ->
                                LaptopCard(
                                    laptop = laptop,
                                    onClick = { onNavigateToDetail(laptop.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
