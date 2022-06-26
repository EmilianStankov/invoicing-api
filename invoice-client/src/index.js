import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import CssBaseline from '@mui/material/CssBaseline';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import InvoiceProcess from './process';

const theme = createTheme({
  palette: {
    mode: 'dark',
  },
});

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <ThemeProvider theme={theme}>
    <CssBaseline />
    <Container component="main" maxWidth="sm">
      <Paper variant="outlined" sx={{ my: { xs: 3, md: 6 }, p: { xs: 2, md: 3 }}}>
        <Typography component="h1" variant="h4" align="center">
          Invoice Processing
        </Typography>
        <React.Fragment>
          <InvoiceProcess/>
        </React.Fragment>
      </Paper>
    </Container>
  </ThemeProvider>
);