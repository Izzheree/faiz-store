package com.faiz0033.faizstore.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.faiz0033.faizstore.R
import com.faiz0033.faizstore.domain.model.Laptop
import com.faiz0033.faizstore.domain.repository.LaptopRepository
import com.faiz0033.faizstore.presentation.common.components.ErrorState
import com.faiz0033.faizstore.presentation.common.components.LoadingState
import com.faiz0033.faizstore.utils.toRupiah
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    laptopId: String,
    laptopRepository: LaptopRepository,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.Factory(laptopRepository, laptopId)
    )
    val uiState by viewModel.uiState.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()
    val deleteError by viewModel.deleteError.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.laptop_details)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(laptopId) }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is DetailUiState.Loading -> LoadingState()
                is DetailUiState.Error -> ErrorState(message = state.message)
                is DetailUiState.Success -> {
                    LaptopDetailContent(laptop = state.laptop)
                }
            }
        }
        
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { 
                    if (!isDeleting) {
                        showDeleteDialog = false
                        viewModel.clearDeleteError()
                    }
                },
                title = { Text(stringResource(R.string.delete_laptop)) },
                text = { 
                    Column {
                        Text(stringResource(R.string.delete_confirmation))
                        if (deleteError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(deleteError!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.deleteLaptop(onSuccess = onNavigateBack) },
                        enabled = !isDeleting
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showDeleteDialog = false 
                            viewModel.clearDeleteError()
                        },
                        enabled = !isDeleting
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun LaptopDetailContent(laptop: Laptop) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = laptop.imageUrl,
            contentDescription = laptop.name,
            placeholder = painterResource(R.drawable.loading_img),
            error = painterResource(R.drawable.loading_img),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = laptop.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = laptop.brand,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = laptop.price.toRupiah(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        DetailItem(label = stringResource(R.string.category), value = laptop.category ?: "-")
        DetailItem(label = stringResource(R.string.processor), value = laptop.processor ?: "-")
        DetailItem(label = stringResource(R.string.ram), value = laptop.ram ?: "-")
        DetailItem(label = stringResource(R.string.storage), value = laptop.storage ?: "-")
        DetailItem(label = stringResource(R.string.stock), value = laptop.stock?.toString() ?: "0")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = laptop.description ?: stringResource(R.string.no_description_available),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
