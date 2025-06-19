// Dashboard Chart JavaScript
function initializeExpenseChart(monthlyTotals) {
    try {
        console.log('Monthly totals received:', monthlyTotals);

        // Check if data exists and is not empty
        if (!monthlyTotals || Object.keys(monthlyTotals).length === 0) {
            console.warn('No monthly totals data available');
            document.querySelector('.chart-container').innerHTML =
                '<div class="alert alert-warning">No data available for chart</div>';
            return;
        }

        // Extract months and totals
        var months = Object.keys(monthlyTotals);
        var totals = Object.values(monthlyTotals);

        console.log('Months:', months);
        console.log('Totals:', totals);

        // Get canvas context
        var ctx = document.getElementById('expenseChart');
        if (!ctx) {
            console.error('Canvas element not found');
            return;
        }

        // Create the chart
        var chart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: months,
                datasets: [{
                    label: 'Monthly Expenses',
                    data: totals,
                    backgroundColor: function(context) {
                        const chart = context.chart;
                        const {ctx, chartArea} = chart;
                        if (!chartArea) return 'rgba(13, 110, 253, 0.6)';

                        const gradient = ctx.createLinearGradient(0, chartArea.bottom, 0, chartArea.top);
                        gradient.addColorStop(0, 'rgba(13, 110, 253, 0.2)');
                        gradient.addColorStop(0.5, 'rgba(13, 110, 253, 0.4)');
                        gradient.addColorStop(1, 'rgba(13, 110, 253, 0.8)');
                        return gradient;
                    },
                    borderColor: 'rgba(13, 110, 253, 1)',
                    borderWidth: 2,
                    borderRadius: 8,
                    borderSkipped: false,
                    hoverBackgroundColor: 'rgba(13, 110, 253, 0.9)',
                    hoverBorderColor: 'rgba(13, 110, 253, 1)',
                    hoverBorderWidth: 3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                aspectRatio: 2.2,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        titleColor: 'white',
                        bodyColor: 'white',
                        borderColor: 'rgba(13, 110, 253, 1)',
                        borderWidth: 1,
                        cornerRadius: 8,
                        displayColors: false,
                        callbacks: {
                            title: function(context) {
                                return context[0].label + ' Expenses';
                            },
                            label: function(context) {
                                return '₹' + context.parsed.y.toLocaleString('en-IN');
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)',
                            drawBorder: false
                        },
                        ticks: {
                            color: '#6c757d',
                            font: {
                                family: 'Roboto',
                                size: 12
                            },
                            callback: function(value) {
                                return '₹' + (value / 1000) + 'k';
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            color: '#495057',
                            font: {
                                family: 'Roboto',
                                size: 12,
                                weight: '500'
                            }
                        }
                    }
                },
                layout: {
                    padding: {
                        top: 20,
                        right: 20,
                        bottom: 10,
                        left: 10
                    }
                },
                animation: {
                    duration: 1500,
                    easing: 'easeInOutCubic'
                },
                interaction: {
                    intersect: false,
                    mode: 'index'
                }
            }
        });

        console.log('Chart created successfully');

    } catch (error) {
        console.error('Error creating chart:', error);
        document.querySelector('.chart-container').innerHTML =
            '<div class="alert alert-danger">Error loading chart: ' + error.message + '</div>';
    }
}